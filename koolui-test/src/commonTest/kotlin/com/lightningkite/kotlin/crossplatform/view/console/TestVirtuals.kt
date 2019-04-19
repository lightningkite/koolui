package com.lightningkite.kotlin.crossplatform.view.console

class TestVirtuals {

//    class TestContent(
//            var image: Image = Image.Bundled(""),
//            var title:String = "Title",
//            var body:String = "This is some body"
//    )
//
//    fun <T> ViewFactory<T>.testView(content:ObservableProperty<TestContent>) = vertical(
//            0f,
//            Gravity.TopCenter to header(content.transform { it.title }),
//            Gravity.TopFill to image(Point(200f, 200f), content.transform { it.image }),
//            Gravity.BottomFill to horizontal(
//                    0f,
//                    Gravity.FillRight to button("Next"){
//                        println("Hello next world!")
//                    },
//                    Gravity.FillLeft to button("Previous"){
//                        println("Hello previous world!")
//                    }
//            ),
//            Gravity.Fill to scrollVertical(body(content.transform { it.body }))
//    )
//
//    @Test
//    fun test(){
//        val content = StandardObservableProperty(TestContent())
//        val virtualView = VirtualViewFactory.testView(content) as VerticalView
//        virtualView.recursiveViews().forEach {
//            println(it)
//        }
//    }
}