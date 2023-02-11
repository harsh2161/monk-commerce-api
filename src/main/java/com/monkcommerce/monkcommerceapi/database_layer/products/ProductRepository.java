package com.monkcommerce.monkcommerceapi.database_layer.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.Category;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.data_objects.products.request.ProductRequest;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.Product;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
public class ProductRepository
{
    private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    private static Integer BATCH_LIMIT = 500;
    @Bean("asyncExecution")
    public boolean getAndStoreProductsFromExternalApi(ArrayList<Category> categories) throws DataException {
        boolean response = true;
        for (var category : categories)
        {
            response = response & getAndStoreProductsFromExternalApi(category.getId()).isSuccess();
        }
        return response;
    }
    @Bean("asyncExecution")
    public ProcessStatus getAndStoreProductsFromExternalApi(String categoryId) throws DataException {
        logger.info("Calling External api in repository started.");
        Integer page = ExternalAPI.DEFAULT_PAGE;
        boolean isResponseTrue = true;
        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(ExternalAPI.getProductWithParams(ExternalAPI.DEFAULT_PRODUCT_LIMIT, page, categoryId), HttpMethod.GET, new HttpEntity<Object>(ExternalAPI.getHeadersWithApiKey(new HashMap<>())), new ParameterizedTypeReference<String>() {});
            var product = response.getBody();

            ProductDTO products = extractProductValue(product);

            if (products.getProducts() != null && products.getProducts().size() > 0)
            {
                isResponseTrue = isResponseTrue & saveAllFetchedProducts(categoryId, products.getProducts());
            }
            else
                break;

            page++;
        }
        if(!isResponseTrue)
        {
            return new ProcessStatus(false,"Product are not saved properly");
        }
        logger.info("Product Data is saved our database");
        return new ProcessStatus(true,ExternalAPI.DATA_SAVED);
    }
    public ProductDTO getProducts(ProductRequest productRequest) throws DataException {
        try {
            logger.info("Get Products from our databases");
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

            ApiFuture<QuerySnapshot> getCategories = baseCollection.document("Categories").collection("product-categories").document(productRequest.getCategoryId()).collection("products").orderBy("customerReviewCount", Query.Direction.DESCENDING).limit(productRequest.getLimit()).offset(productRequest.getPage()*productRequest.getLimit()).get();
            List<QueryDocumentSnapshot> documentsCategory = getCategories.get().getDocuments();

            ProductDTO productDTO = new ProductDTO();
            productDTO.setPage(productRequest.getPage());

            for (var documentCategory : documentsCategory) {
                productDTO.AddCategories(documentCategory.getData());
            }
            logger.info("Successfully got the products from database");
            return productDTO;
        }
        catch (Exception ex)
        {
            logger.error("Getting error : "+ex.getMessage());
            System.out.println(ex.getMessage());
        }

        throw new DataException("Data is not present");
    }
    @Bean("asyncExecution")
    private boolean saveAllFetchedProducts(String categoryId, ArrayList<Product> products)
    {
        try
        {
            logger.info("Get Products from external api and saving that into database");
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
            logger.info("Sucessfully saved Products into our database");
            return Response;
        }
        catch (Exception ex)
        {
            logger.error("Getting error : "+ex.getMessage());
            System.out.println(ex.getMessage());
        }
        return false;
    }

    private boolean updateProductsCounts(String categoryId,ArrayList<Product> products)
    {
        try
        {
            logger.info("Updating Products process started");
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project").document("Categories").collection("product-categories");
            ApiFuture<WriteResult> updateProductsCount = baseCollection.document(categoryId).update("noOfProducts", products.size());
            updateProductsCount.get();
            logger.info("Updating Products process finished");
            return updateProductsCount.isDone();
        }
        catch (Exception ex)
        {
            logger.error("Getting error : "+ex.getMessage());
            System.out.println(ex.getMessage());
        }
        return false;
    }
    private ProductDTO extractProductValue(String product)
    {
        ProductDTO productDTO = new ProductDTO();
        try {
            Gson gson = new Gson();
            productDTO = gson.fromJson(product, ProductDTO.class);
        }catch (Exception ex) {logger.error(ex.getMessage());}
        return productDTO;
    }
}
