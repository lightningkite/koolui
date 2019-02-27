# ReacKtive

By [Lightning Kite](https://lightningkite.com)

Maven: [![Download](https://api.bintray.com/packages/lightningkite/com.lightningkite.krosslin/koolui/images/download.svg) ](https://bintray.com/lightningkite/com.lightningkite.krosslin/koolui/_latestVersion) 

Abstracts view creation for multiplatform projects, allowing you to write a short description of your UI and have it be implemented on all platforms according to how the given platform renders it.


## Design Philosophy

- Material Design by default
- Minimize effort to implement a particular platform
- Interaction with UI components after creation is done strictly through observable properties
- Define what components you have and their arrangement in multiplatform, do styling per platform

## Concepts

- `ViewGenerator` - a plain Kotlin object that contains instructions for rendering a view.
- `ViewFactory` - An interface for creating views for a particular platform.  The views returned could be of any type, so don't expect to interact with them after creation.  You will probably extend this interface for your particular application to add new view types as necessary.  Concrete implementations of the base one already exist for each platform.
- `Theme` - A general color theme to use in the app.  Frequently passed in to concrete implementations of `ViewFactory` to give it a color scheme.
- From Reacktive:
    - `ObservableProperty` - a property you can observe
    - `MutableObservableProperty` - a property you can observe and change
    - `(Mutable)ObservableList` - a list you can observe, frequently used to keep track of navigation stacks

## Gradle Inclusion

```
repositories {
    maven { url 'https://dl.bintray.com/lightningkite/com.lightningkite.krosslin' }
    ...
}
...
dependencies {
    ...
    //Depending on the version you need
    api "com.lightningkite:koolui-metadata:${kooluiVersion}"
    api "com.lightningkite:koolui-jvmvirtual:${kooluiVersion}"
    api "com.lightningkite:koolui-javafx:${kooluiVersion}"
    api "com.lightningkite:koolui-android:${kooluiVersion}"
    api "com.lightningkite:koolui-js:${kooluiVersion}"
}
```

## Suggested Platform Setup

### Multiplatform

Define how your views work on this platform:

```kotlin
//settings.kt

interface MyViewFactory<VIEW> : ViewFactory<VIEW> {}
typealias MyViewGenerator<VIEW> = ViewGenerator<MyViewFactory<VIEW>, VIEW>

```

For your root view, create a `ViewGenerator` like this:

```kotlin
class MainVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "KotlinX UI Test"

    //This will be our navigation stack.
    val stack = WrapperObservableList<ViewGenerator<ViewFactory<VIEW>, VIEW>>()

    init {
        //On start, add a different view generator to the stack
        stack.push(SelectorVG(stack))
    }

    //The view for this ViewGenerator is created here:
    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        window(
                dependency = dependency,
                stack = stack,
                tabs = listOf()
        )
    }
}
```

### Android

Use this code for your main activity:

```kotlin
class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<View>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureUi(this, R.drawable.ic_notifications)

        val factory = AndroidMaterialViewFactory(this, Theme.dark())
        val view = factory.contentRoot(main.generate(factory))
        view.lifecycle.alwaysOn = true
        setContentView(view)
    }
}
```

### JS Setup

Add this to your main function:

```kotlin
window.onload = {
    with(HtmlViewFactory(Theme.dark())){
        applyDefaultCss()
        val view = rootContainer(MainVG<HTMLElement>().generate(this))
        view.lifecycle.alwaysOn = true
        document.body!!.appendChild(view)
    }
}
```

## JavaFX Setup

Use this for your application:

```kotlin
class Main : Application() {

    companion object {
        val view = MainVG<Node>()
    }

    override fun start(primaryStage: Stage) {
        configureUi(Main::class.java.classLoader, primaryStage)
        val view = with(MaterialJavaFxViewFactory(Theme.dark(), resourceFetcher = { javaClass.getResourceAsStream(it) }, scale = 1.0)) {
            val v: Node = view.generate(this)
            v.lifecycle.alwaysOn = true
            v
        }
        primaryStage.scene = Scene(view as Parent)
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}
```