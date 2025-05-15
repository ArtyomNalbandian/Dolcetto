package com.example.dolcetto.data.repository

import androidx.compose.animation.core.snap
import com.example.dolcetto.domain.model.CartItem
import com.example.dolcetto.domain.model.TestUserData
import com.example.dolcetto.domain.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val currentUserId
        get() = auth.currentUser?.uid ?: throw Exception("User not logged in")

//    fun getCartDep() = flow<List<CartItem>> {
//        db.collection("users").document(currentUserId)
//            .addSnapshotListener { snapshot, _ ->
//                val cart = snapshot?.get("cart")?.let {
//                    (it as List<Map<String, Any>>).map { doc ->
//                        CartItem(
//                            dishId = doc["dishId"] as String,
//                            quantity = (doc["quantity"] as Long).toInt(),
//                            price = doc["price"] as Double,
//                            name = doc["name"] as String,
//                            imageUrl = doc["imageUrl"] as String
//                        )
//                    }
//                } ?: emptyList()
//                emit(cart)
//            }
//    }

    fun getCart() = callbackFlow<List<CartItem>> {
        val listener = db.collection("users").document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val cart = snapshot?.get("cart").let {
                    (it as List<Map<String, Any>>).map { doc ->
                        CartItem(
                            dishId = doc["dishId"] as String,
                            quantity = (doc["quantity"] as Long).toInt(),
                            price = doc["price"] as Double,
                            name = doc["name"] as String,
                            imageUrl = doc["imageUrl"] as String
                        )
                    } ?: emptyList()
                }
                trySend(cart).isSuccess

//                snapshot?.toObject(CartItem::class.java)?.let { cartItems ->
//                    trySend(listOf(cartItems))
//                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun addToCart(item: CartItem) {
        db.collection("users").document(currentUserId)
            .update("cart", FieldValue.arrayUnion(item.toMap()))
            .await()
    }

    suspend fun updateCartItem(item: CartItem) {
        val userRef = db.collection("users").document(currentUserId)
        db.runTransaction { transaction ->
            val user = transaction.get(userRef).toObject(UserData::class.java)!!
            val newCart = user.cart.toMutableList()
            val index = newCart.indexOfFirst { it.dishId == item.dishId }
            if (index != -1) {
                newCart[index] = item
            }
            transaction.update(userRef, "cart", newCart.map { it.toMap() })
        }.await()
    }

    suspend fun clearCart() {
        db.collection("users").document(currentUserId)
            .update("cart", emptyList<CartItem>())
            .await()
    }

    private fun CartItem.toMap() = mapOf(
        "dishId" to dishId,
        "quantity" to quantity,
        "price" to price,
        "name" to name,
        "imageUrl" to imageUrl
    )

}