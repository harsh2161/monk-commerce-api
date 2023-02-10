package com.monkcommerce.monkcommerceapi.database_layer.authentication;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthRegisterResponse;
import com.monkcommerce.monkcommerceapi.data_objects.authentication.AuthenticationRequest;
import com.monkcommerce.monkcommerceapi.data_objects.register.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationRepository
{
    private static Firestore firebaseDatabase;
    private static CollectionReference baseCollection;
    public AuthenticationRepository()
    {
        try {

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    public boolean registerUser(RegisterRequest request) throws ExecutionException, InterruptedException
    {
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        if(findUserByEmail(request.getEmail()) != null)
        {
            // throw custom exception
        }

        ApiFuture<WriteResult> createUser = baseCollection.document("users").collection("user-details").document(request.getId()).set(request);
        return createUser.isDone();
    }
    public AuthRegisterResponse findUserByEmail(String email) throws ExecutionException, InterruptedException
    {
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        ApiFuture<QuerySnapshot> userSearchByIdQuery = baseCollection.document("users").collection("user-details").whereEqualTo("email",email).get();
        List<QueryDocumentSnapshot> documents = userSearchByIdQuery.get().getDocuments();

        if (documents.size() > 0)
            return documents.get(0).toObject(AuthRegisterResponse.class);

        return null;
    }
    public AuthRegisterResponse authenticateUser(AuthenticationRequest request) throws ExecutionException, InterruptedException
    {
        firebaseDatabase = FirestoreClient.getFirestore();
        baseCollection = firebaseDatabase.collection("WebProjects").document("Monk-Commerce-Api").collection("Backend-Project");

        var getUserDetails = findUserByEmail(request.getEmail());

        if(getUserDetails == null)
        {
            // throw custom exception
        }

        return getUserDetails;
    }
}
