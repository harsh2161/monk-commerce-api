package com.monkcommerce.monkcommerceapi.database_layer.category;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.Category;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
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
public class CategoriesRepository
{
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    private static Integer BATCH_LIMIT = 500;
    public ProcessStatus getAndStoreCategoriesFromExternalApi() throws DataException {
        Integer page = ExternalAPI.DEFAULT_PAGE;
        CategoriesDTO categoriesDTO = new CategoriesDTO();

        while(true)
        {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CategoriesDTO> response = restTemplate.exchange(ExternalAPI.getCategoriesWithParams(ExternalAPI.DEFAULT_LIMIT,page), HttpMethod.GET, new HttpEntity<Object>(ExternalAPI.getHeadersWithApiKey(new HashMap<>())) , new ParameterizedTypeReference<CategoriesDTO>() {});

            var categories = response.getBody();

            if(categories != null && categories.getCategories() != null && categories.getCategories().size() > 0)
                categoriesDTO.AddCategories(categories.getCategories());
            else
                break;

            page++;
        }

        if(categoriesDTO.getCategories() != null && categoriesDTO.getCategories().size() > 0)
            if(!saveAllFetchedCategories(categoriesDTO.getCategories()))
                throw new DataException("Data is not saved to our database");

        return new ProcessStatus(true,ExternalAPI.DATA_SAVED);
    }

    public static boolean saveAllFetchedCategories(ArrayList<Category> categories) {
        try {
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project").document("Categories").collection("product-categories");

            WriteBatch batch = firebaseDatabase.batch();
            boolean Response = true;

            for (var category : categories) {
                DocumentReference isDocumentPresent = baseCollection.document(category.getId());
                ApiFuture<DocumentSnapshot> future = isDocumentPresent.get();
                DocumentSnapshot document = future.get();
                if (!document.exists()) {
                    batch.create(baseCollection.document(category.getId()), category);
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
            return Response;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public CategoriesDTO getCategories(CategoryRequest request)
    {
        try {
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

            ApiFuture<QuerySnapshot> getCategories = baseCollection.document("Categories").collection("product-categories").orderBy("noOfProducts", Query.Direction.DESCENDING).limit(request.getLimit()).offset(request.getPage()*request.getLimit()).get();
            List<QueryDocumentSnapshot> documentsCategory = getCategories.get().getDocuments();

            CategoriesDTO categoriesDTO = new CategoriesDTO();
            categoriesDTO.setPage(request.getPage());

            for (var documentCategory : documentsCategory) {
                categoriesDTO.AddCategories(documentCategory.toObject(Category.class));
            }
            return categoriesDTO;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return new CategoriesDTO();
    }
}
