package hoods.com.jetai.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

import hoods.com.jetai.Graph
import hoods.com.jetai.data.models.ModelName
import hoods.com.jetai.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow



class PhotoReasoningRepository (

    private val generativeModel: GenerativeModel= Graph.generativeModel(ModelName.MULTIMODAL.modelName)
){
//    suspend fun reason(
//        userInput: String, selectedImage:List<Bitmap>
//    ): Flow<Response<String>> = flow {
//        val prompt="Look at the image(s) and then answer the following question"
//        try {
//            emit(Response.Loading())
//            val inputContent = content {
//
//                for(bitmap in selectedImage){
//                    image(bitmap)
//
//                }
//                text(prompt)
//
//
//            }
//            var outputContent=""
//            generativeModel.generateContentStream(inputContent).collectLatest {
//                response->
//                outputContent += response.text
//                emit(Response.Success(outputContent))
//            }
//
//        } catch (e:Exception){
//            emit(Response.Error(e.cause))
//        }
//    }

    suspend fun reason(
        userInput: String, selectedImage: List<Bitmap>
    ): Flow<Response<String>> = channelFlow {
        val prompt = "Look at the image(s) and then answer the following question:$userInput"
        try {
            send(Response.Loading())  // ✅ Use send() instead of emit()

            val inputContent = content {
                for (bitmap in selectedImage) {
                    image(bitmap)
                }
                text(prompt)
            }

            var outputContent = ""
            generativeModel.generateContentStream(inputContent).collectLatest { response ->
                outputContent += response.text
                send(Response.Success(outputContent))  // ✅ Use send() inside collectLatest
            }

        } catch (e: Exception) {
            send(Response.Error(e.cause))  // ✅ Use send() instead of emit()
        }
    }
}