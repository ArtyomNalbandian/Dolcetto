package com.example.dolcetto

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    fun getMenu() = callbackFlow<List<Dish>> {
        val listener = db.collection("dishes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Закрываем поток с ошибкой
                    return@addSnapshotListener
                }

                snapshot?.toObjects(Dish::class.java)?.let { dishes ->
                    trySend(dishes) // Используем trySend вместо emit
                }
            }

        // Очистка при отмене корутины
        awaitClose { listener.remove() }
    }

    suspend fun addDish(dish: Dish): Resource<String> {
        return try {
            val docRef = db.collection("dishes").document()
            val newDish = dish.copy(id = docRef.id)
            docRef.set(newDish).await()
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding dish")
        }
    }

    suspend fun updateDish(dish: Dish): Resource<Unit> {
        return try {
            db.collection("dishes").document(dish.id)
                .set(dish)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating dish")
        }
    }

    suspend fun deleteDish(dishId: String): Resource<Unit> {
        return try {
            db.collection("dishes").document(dishId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting dish")
        }
    }

}