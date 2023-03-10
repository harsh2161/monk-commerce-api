package com.monkcommerce.monkcommerceapi.database_layer.authentication;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.monkcommerce.monkcommerceapi.custom_exceptions.DataException;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthenticationRequest;
import com.monkcommerce.monkcommerceapi.data_objects.register.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationRepository
{
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationRepository.class);
    public boolean registerUser(RegisterRequest request) throws ExecutionException, InterruptedException, DataException
    {
        logger.info("Register Process of Database Layer "+request.getEmail()+" is started.");
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        if(findUserByEmail(request.getEmail()) != null)
        {
            logger.error("User Already Exist : "+request.getEmail());
            throw new DataException("User With This Email : "+request.getEmail()+" Already Exist In Database");
        }

        ApiFuture<WriteResult> createUser = baseCollection.document("users").collection("user-details").document(request.getId()).set(request);
        createUser.get();
        logger.info("Register Process of Database Layer "+request.getEmail()+" is ended.");
        return createUser.isDone();
    }
    public AuthRegisterResponse findUserByEmail(String email) throws ExecutionException, InterruptedException
    {
        logger.info("searching for user email Process of Database Layer "+email+" is started.");
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        ApiFuture<QuerySnapshot> userSearchByIdQuery = baseCollection.document("users").collection("user-details").whereEqualTo("email",email).get();
        List<QueryDocumentSnapshot> documents = userSearchByIdQuery.get().getDocuments();

        if (documents.size() == 0)
            return null;

        logger.info("searching for user email Process of Database Layer "+email+" is founded.");
        return documents.get(0).toObject(AuthRegisterResponse.class);
    }
    public AuthRegisterResponse authenticateUser(AuthenticationRequest request) throws ExecutionException, InterruptedException, DataException {
        logger.info("authentication Process of Database Layer "+request.getEmail()+" is started.");
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        var getUserDetails = findUserByEmail(request.getEmail());

        if(getUserDetails == null)
        {
            logger.error("User Not Exist : "+request.getEmail());
            throw new DataException("User With This Email : "+request.getEmail()+" Not Exist In Database");
        }
        logger.info("authentication Process of Database Layer "+request.getEmail()+" is ended.");
        return getUserDetails;
    }
}
