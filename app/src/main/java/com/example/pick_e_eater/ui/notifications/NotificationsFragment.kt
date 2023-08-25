package com.example.pick_e_eater.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.pick_e_eater.R
import com.example.pick_e_eater.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    enum class CardFace(val angle: Float) {
        Front(0f) {
            override val next: CardFace
                get() = Back
        },
        Back(180f) {
            override val next: CardFace
                get() = Front
        };

        abstract val next: CardFace
    }
    enum class BoxState {
        Start,
        End
    }

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }
    @OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeContainer = view.findViewById<FrameLayout>(R.id.compose_container)

        // Create a ComposeView and set the content using setContent
        val context = composeContainer.context
        val composeView = ComposeView(context)
        composeContainer.addView(composeView)

        composeView.setContent {
            AwesomeCarousel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    @Preview(showBackground = true)
    fun AwesomeCarousel(
        pageCount: Int = 25,
        pagerState: PagerState = rememberPagerState(),
//        autoScrollDuration: Long = 200L
    ) {
        var cardFace by remember { mutableStateOf(CardFace.Front) }
        var resultsVisible by remember { mutableStateOf(false) }
        // AUTO SCALE
        var boxState by remember { mutableStateOf(BoxState.Start) }
        val scale:Dp by animateDpAsState(
            targetValue = if (boxState == BoxState.Start) 1.dp else 200.dp,
            animationSpec = keyframes {
                durationMillis = 1000
                delayMillis = 50
            },
            finishedListener = {
                cardFace = cardFace.next
            },
        )
        // AUTO ANIMATE
        if (boxState == BoxState.Start) {
            with(pagerState) {
                var currentPageKey by remember { mutableStateOf(0) }
                LaunchedEffect(key1 = currentPageKey) {
                    launch {
//                        delay(timeMillis = autoScrollDuration)
                        val nextPage = (currentPage + 1).mod(pageCount)
                        animateScrollToPage(page = nextPage)
                        currentPageKey = nextPage
                        // TODO: hard coded to 8 spins but will change later
                        if (currentPage == 8) {
                            boxState = BoxState.End
                        }
                    }
                }
            }
        }
        // TODO: May want to change this to make it look more like a wheel spin animation
        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            FlipCard(
                cardFace = cardFace,
                front = { questionBlock(scale) },
                back = { questionBlock(scale) },
                onAnimationDone = { resultsVisible = true},
            )
        }
        viewResultsBtn(resultsVisible)
    }

    @Composable
    fun FlipCard(
        cardFace: CardFace,
        modifier: Modifier = Modifier,
        back: @Composable () -> Unit = {},
        front: @Composable () -> Unit = {},
        onAnimationDone:() -> Unit,
    ) {

        val rotation = animateFloatAsState(
            targetValue = cardFace.angle,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing,
            ),
            finishedListener = {
                onAnimationDone()
            }
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * density
                }
                .wrapContentSize(Alignment.Center)
                .wrapContentHeight(Alignment.CenterVertically),
        ) {
            if (rotation.value <= 90f) {
                front()
            } else {
                Column(
                    Modifier
                        .graphicsLayer {
                            rotationY = 180f
                        },
                ) {
                    back()
                }
            }
        }
    }

    @Composable
    fun questionBlock(scale: Dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(20.dp)
                .background(Color.Black)
                .padding(2.dp)
                .background(Color.Yellow)
                .padding(10.dp)
                .fillMaxWidth()
                .padding(top = scale, bottom = scale)
        ) {
            Text(
                text = "?",
                color = Color.Black,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )
        }
    }

    @Composable
    fun viewResultsBtn(visible: Boolean) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = {
                        //TODO: take you to the results page
                    })
                {
                    Text("View Results")
                }
            }
        }
    }
}