# ReacKtive

By [Lightning Kite](https://lightningkite.com)

Maven: [![Download](https://api.bintray.com/packages/lightningkite/com.lightningkite.krosslin/koolui/images/download.svg) ](https://bintray.com/lightningkite/com.lightningkite.krosslin/koolui/_latestVersion) 

Abstracts view creation for multiplatform projects, allowing you to write a short description of your UI and have it be implemented on all platforms according to how the given platform renders it.

[Video by Imgur](https://i.imgur.com/G0a0yM9.mp4)

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
    
## To Do / Help Requests

- JS could be made to look much better
- iOS has been started, but still needs to be finished

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

### Android Setup

Use this code for your main activity:

```kotlin
class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<View>()
    }

    class Factory(
            activity: AccessibleActivity
    ) : MyViewFactory<View>, ViewFactory<View> by AndroidMaterialViewFactory(activity, Theme.dark()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationAccess.init(this, R.drawable.ic_notifications)

        setContentView(Factory(this).contentRoot(main))
    }
}
```

### JS Setup

Add this to your main file:

```kotlin
class Factory() : MyViewFactory<HTMLElement>, ViewFactory<HTMLElement> by HtmlViewFactory(Theme.dark()) {}

fun main(args: Array<String>) {
    window.onload = {
        document.body!!.appendChild(
                Factory().contentRoot(MainVG<HTMLElement>())
        )
    }
}

```

## JavaFX Setup

Use this for your application:

```kotlin
class Main : Application() {

    companion object {
        val mainVg = MainVG<Node>()
    }

    class Factory() : MyViewFactory<Node>, ViewFactory<Node> by MaterialJavaFxViewFactory(Theme.dark(), scale = 1.0) {}

    override fun start(primaryStage: Stage) {
        ApplicationAccess.init(Main::class.java.classLoader, primaryStage)
        primaryStage.scene = Scene(Factory().contentRoot(mainVg) as Parent)
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}

```