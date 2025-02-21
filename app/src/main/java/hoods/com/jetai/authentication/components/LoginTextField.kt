package hoods.com.jetai.authentication.components


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginTextField(
    modifier: Modifier=Modifier,
    value:String,
    onValueChange:(String)->Unit,
    labelText:String,
    leadingIcon:ImageVector?=null,
    keyboardType: KeyboardType= KeyboardType.Text,
    visualTransformation: VisualTransformation=VisualTransformation.None,
    isError:Boolean=false,
    imeAction: ImeAction = ImeAction.Next, // Dodali smo ImeAction
    onNext: (() -> Unit)? = null, // Callback za Next dugme
    onDone: (() -> Unit)? = null // Callback za Done dugme

) {


   OutlinedTextField(value = value,
       onValueChange = onValueChange,
       modifier=modifier,
       label = { Text(text = labelText)},
      leadingIcon = {
          if(leadingIcon!=null) Icon(leadingIcon,null)
      },
       keyboardOptions = KeyboardOptions(keyboardType=keyboardType, imeAction = imeAction ),
       keyboardActions = KeyboardActions(
           onNext = {
               onNext?.invoke() // Prelazak na sledeće polje
           },
                   onDone = {
               onDone?.invoke() // Poziv funkcije za "Done"

           }
       ),
       visualTransformation = visualTransformation,
       shape = RoundedCornerShape(30),
       isError = isError,
       singleLine = true // Sprečava proširivanje teksta u više redova

   )

}