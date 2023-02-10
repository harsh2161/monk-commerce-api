package com.monkcommerce.monkcommerceapi.database_layer.products;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.Product;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
public class ProductRepository
{
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    private static Integer BATCH_LIMIT = 500;

    public ProcessStatus getAndStoreProductsFromExternalApi(String categoryId) throws DataException {
        Integer page = ExternalAPI.DEFAULT_PAGE;
        ProductDTO productDTO = new ProductDTO();
        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(ExternalAPI.getProductWithParams(ExternalAPI.DEFAULT_PRODUCT_LIMIT, page, categoryId), HttpMethod.GET, new HttpEntity<Object>(ExternalAPI.getHeadersWithApiKey(new HashMap<>())), new ParameterizedTypeReference<String>() {});
            var product = response.getBody();

            Gson json = new Gson();
            ProductDTO products = json.fromJson(product, ProductDTO.class);

            if (products != null && products.getProducts() != null && products.getProducts().size() > 0)
                productDTO.AddCategories(products.getProducts());
            else
                break;

            page++;
        }
        if (productDTO.getProducts() != null && productDTO.getProducts().size() > 0)
            if (!saveAllFetchedProducts(categoryId, productDTO.getProducts()))
                throw new DataException("Data is not saved to our database");

        return new ProcessStatus(true,ExternalAPI.DATA_SAVED);
    }

    public ProductDTO getProducts(ProductRequest productRequest)
    {
        try {
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

            ApiFuture<QuerySnapshot> getCategories = baseCollection.document("Categories").collection("product-categories").document(productRequest.getCategoryId()).collection("products").orderBy("customerReviewCount", Query.Direction.DESCENDING).limit(productRequest.getLimit()).offset(productRequest.getPage()*productRequest.getLimit()).get();
            List<QueryDocumentSnapshot> documentsCategory = getCategories.get().getDocuments();

            ProductDTO productDTO = new ProductDTO();
            productDTO.setPage(productRequest.getPage());

            for (var documentCategory : documentsCategory) {
                productDTO.AddCategories(documentCategory.getData());
            }
            return productDTO;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return new ProductDTO();
    }

    private boolean saveAllFetchedProducts(String categoryId, ArrayList<Product> products)
    {
        try {
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project").document("Categories").collection("product-categories").document(categoryId).collection("products");

            WriteBatch batch = firebaseDatabase.batch();
            boolean Response = true;

            for (var product : products) {
                DocumentReference isDocumentPresent = baseCollection.document(product.getSku()+"");
                ApiFuture<DocumentSnapshot> future = isDocumentPresent.get();
                DocumentSnapshot document = future.get();
                if (!document.exists()) {
                    batch.create(baseCollection.document(product.getSku()+""), product);
                }
                if (batch.getMutationsSize() == BATCH_LIMIT) {
                    ApiFuture<List<WriteResult>> commitCategories = batch.commit();
                    commitCategories.get();
                    Response = Stream.of(commitCategories).allMatch(val -> commitCategories.isDone()) && Response;
                    batch = firebaseDatabase.batch();
                }
            }
            if(batch.getMutationsSize() != 0)
            {
                ApiFuture<List<WriteResult>> commitCategories = batch.commit();
                commitCategories.get();
                Response = Stream.of(commitCategories).allMatch(val -> commitCategories.isDone()) && Response;
            }
            Response = Response & updateProductsCounts(categoryId, products);
            return Response;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    private boolean updateProductsCounts(String categoryId,ArrayList<Product> products)
    {
        try
        {
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project").document("Categories").collection("product-categories");
            ApiFuture<WriteResult> updateProductsCount = baseCollection.document(categoryId).update("noOfProducts", products.size());
            updateProductsCount.get();
            return updateProductsCount.isDone();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
