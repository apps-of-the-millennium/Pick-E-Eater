package com.example.pick_e_eater.ui.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.pick_e_eater.model.Restaurant

fun String?.sanitize(): String {
    return if (this.isNullOrEmpty()) "N/A" else this
}

@Composable
fun RestaurantComposable(rest: Restaurant) {
    val area = rest.rest_area.sanitize()
    val address = rest.rest_address.sanitize()
    val hours = listOf(
        "Monday: " + rest.hours_mon.sanitize(),
        "Tuesday: " + rest.hours_tues.sanitize(),
        "Wednesday: " + rest.hours_wed.sanitize(),
        "Thursday: " + rest.hours_thurs.sanitize(),
        "Friday: " + rest.hours_fri.sanitize(),
        "Saturday: " + rest.hours_sat.sanitize(),
        "Sunday: " + rest.hours_sun.sanitize(),
    )
    val website = rest.rest_url.sanitize()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .size(300.dp)
        ) {
            Card(modifier = Modifier
                .padding(10.dp),
                shape = RectangleShape,
                elevation = 8.dp,
            ){
                Image(painter = rememberAsyncImagePainter(model = rest.rest_photo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Box(modifier = Modifier
            .weight(5f)
        ) {
            Column(verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = rest.rest_name, fontSize = 22.sp, color = Color.Gray,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                    Text(text = "|", fontSize = 22.sp, color = Color.Gray,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                    Text(text = rest.rest_type!!, fontSize = 22.sp, color = Color.Gray)
                }

                Divider(modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp),
                    4.dp,
                    Color.Gray
                )

                Text(text = "Area: $area", fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 6.dp)
                        .fillMaxWidth()
                )
                
                Text(text = "Address: $address", fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 6.dp)
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Hours: ", fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = 10.dp, top = 6.dp, end = 6.dp)
                    )
                    GetHours(hours)
                }

                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 6.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = "Website: ", fontSize = 24.sp)
                    HyperlinkInSentence(
                        sourceText = "",
                        hyperlinkText = "Click Here",
                        endText = "",
                        uri = website
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Button(modifier = Modifier
                    .height(40.dp)
                    .width(125.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0Xff62a3db)
                    ),
                    shape = RoundedCornerShape(15),
                    onClick = {}
                ) {
                    Text(text = "RE-SPIN", fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
            }
        }
    }
}

@Composable
fun GetHours(listOfHours: List<String>){
    val expanded = remember{ mutableStateOf(false) }
    val arrow = remember { mutableStateOf(Icons.Filled.KeyboardArrowDown) }

    Surface() {
        Box() {
            Row(modifier = Modifier
                    .clickable {
                        expanded.value = !expanded.value
                        arrow.value = Icons.Filled.KeyboardArrowUp
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "see hours", fontSize = 24.sp, color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 6.dp)
                )
                Icon(imageVector = arrow.value,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 6.dp)
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = {
                        expanded.value = false
                        arrow.value = Icons.Filled.KeyboardArrowDown
                    },
                    offset = DpOffset(30.dp, 9.dp)
                ) {
                    listOfHours.forEach {
                        DropdownMenuItem(onClick = {}) {
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HyperlinkInSentence(sourceText: String, hyperlinkText: String, endText: String, uri: String) {

    val annotatedString = buildAnnotatedString {
        append(sourceText)
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue, fontSize = 24.sp)) {
            append(hyperlinkText)
            addStringAnnotation(
                tag = "URL",
                annotation = uri,
                start = length - hyperlinkText.length,
                end = length
            )
        }
        append(endText)
    }
    val uriHandler = LocalUriHandler.current
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    RestaurantComposable(
        rest = Restaurant(
            rest_name = "100 Percent Korean",
            rest_type = "Korean",
            rest_photo = "https://s3-media0.fl.yelpcdn.com/bphoto/5NlayoVpyg-B1uCctxZB7Q/348s.jpg",
            rest_url = "https://www.yelp.ca/biz/100-percent-korean-toronto?osq=Restaurants",
            rest_area = "Milliken",
            rest_address = "4779 Steeles Avenue EToronto, ON M1V",
            rest_lat = 43.82529,
            rest_long = -79.29861,
            hours_mon = "12:00 PM - 9:00 PM",
            hours_tues = "Closed",
            hours_wed = "12:00 PM - 9:00 PM",
            hours_thurs = "12:00 PM - 9:00 PM",
            hours_fri = "12:00 PM - 9:00 PM",
            hours_sat = "12:00 PM - 9:00 PM",
            hours_sun = "12:00 PM - 9:00 PM"
        )
    )
}