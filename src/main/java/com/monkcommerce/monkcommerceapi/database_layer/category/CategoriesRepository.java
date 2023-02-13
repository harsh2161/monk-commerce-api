package com.monkcommerce.monkcommerceapi.database_layer.category;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.monkcommerce.monkcommerceapi.common.APICategoryRequestor;
import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.custom_exceptions.RateLimitException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.request.CategoryRequest;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.Category;
import com.monkcommerce.monkcommerceapi.data_objects.process.ProcessStatus;
import com.monkcommerce.monkcommerceapi.database_layer.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoriesRepository
{
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    private static final Logger logger = LoggerFactory.getLogger(CategoriesRepository.class);
    private static Integer BATCH_LIMIT = 500;
    private static final ProductRepository productRepository = new ProductRepository();
    private static final APICategoryRequestor apiCategoryRequestor = new APICategoryRequestor();
    public ProcessStatus getAndStoreCategoriesFromExternalApi() throws DataException, InterruptedException
    {
        logger.info("Calling External api in repository started.");

        if(!getTheCategoryDataResult(ExternalAPI.DEFAULT_PAGE, true))
        {
            logger.error("Data is not saved to our database");
            throw new DataException("Data is not saved to our database");
        }
        logger.info("Data is saved our database");
        return new ProcessStatus(true,ExternalAPI.DATA_SAVED);
    }
    @Bean("asyncExecution")
    public static boolean saveAllFetchedCategories(ArrayList<Category> categories)
    {
        try {
            logger.info("Saving fetched data from third party api to our database");
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
            logger.info("Saved the data to our servers");
            Response = Response & productRepository.getAndStoreProductsFromExternalApi(categories);
            return Response;
        }
        catch (Exception ex)
        {
            logger.error("Getting error : "+ex.getMessage());
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public CategoriesDTO getCategories(CategoryRequest request) throws DataException {
        try {
            logger.info("Get Categories from our databases");
            firebaseDatabase = FirestoreClient.getFirestore();
            baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

            ApiFuture<QuerySnapshot> getCategories = baseCollection.document("Categories").collection("product-categories").orderBy("noOfProducts", Query.Direction.DESCENDING).limit(request.getLimit()).offset(request.getPage()*request.getLimit()).get();
            List<QueryDocumentSnapshot> documentsCategory = getCategories.get().getDocuments();

            CategoriesDTO categoriesDTO = new CategoriesDTO();
            categoriesDTO.setPage(request.getPage());

            for (var documentCategory : documentsCategory) {
                categoriesDTO.AddCategories(documentCategory.toObject(Category.class));
            }
            logger.info("Successfully got the categories from database");
            return categoriesDTO;
        }
        catch (Exception ex)
        {
            logger.error("Getting error : "+ex.getMessage());
            System.out.println(ex.getMessage());
        }
        throw new DataException("Data is not present");
    }
    private boolean getTheCategoryDataResult(Integer page,boolean isResponseTrue) throws InterruptedException {
        try
        {
            var categories = apiCategoryRequestor.makeCategoriesRequest(page);
            if(categories != null && categories.getCategories() != null && categories.getCategories().size() > 0)
            {
                logger.info("Fetched External api in repository completed.");
                isResponseTrue = isResponseTrue & saveAllFetchedCategories(categories.getCategories());
                page++;
                getTheCategoryDataResult(page,isResponseTrue);
            }
            else
                return isResponseTrue;
        }
        catch (RateLimitException ex)
        {
            TimeUnit.SECONDS.sleep(3);
        }
        return isResponseTrue;
    }
}
