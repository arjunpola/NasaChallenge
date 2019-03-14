# NasaChallenge
An app that illustrates the use of NASA Image Library API to search for images by taking any query string as user input.

The app is divided into 3 pages defined by following
1. **Search Page**: User can enter any query and click "Search" button to fetch the data from NASA Image Library API.

2. **Results Page**: List of first page search results which shows Title text and Thumbnail image for each result. Scrolling to the bottom of the list fetches next page. results until the maximum page number(limited to 100) is reached.

3. **Details Page**: Clicking on any of the search result from Results page will show more information about that result.

***Note: For large screen devices the the Results page and Details page are displayed side by side. (Master/Detail layout)***

#### Installing
To install the app on device/emulator, navigate to project folder and run ./gradlew installDebug. Find the app "NasaChallenge" on the device/emulator.

#### Networking
The app implements basic caching logic to avoid redundant searches. This is done by usage of Android's ViewModel architecture components. Some of the rules around searching are:
- If the query does not change between subsequent searches then previously cached results would be served.
- If the activity is destroyed due to configuration changes, search results would still be persisted.
- If the query does not change and the search page being requested is lower than previously fetched page then no new search operation will be performed.

### Libraries Used

**Retrofit**: This library provides a very clean way of grouping networking operations by creating interfaces. It also makes it very convenient to pass any parameters to an API by treating them as simple method parameters. Retrofit in turn uses OkHttp which is an industry standard library for Android clients to perform networking operations. Retrofit also provides options to configure a custom client and any request interceptors required for error logging, analytics etc.

**Gson**: It is used as an adapter with Retrofit to convert the JSON API response into Java POJOs. I choose Gson based on my familiarity using it. It is easy to use and provides nice interface that can be overriden to perform any custom JSON parsing.

**Picasso**: It is a lightweight library which provides helper methods to fetch images. Glide and Fresco are other libraries which also provide similar helper methods. Here, I choose Picasso based on prior usage. Fresco requires a little more setup than Picasso or Glide.

**ReactiveAndroid**: This library provides APIs to perform asynchronous operations operations. It embraces the functional programming concepts and is great for Networking, Database, Animations etc. Though, I have used this library, it is not actually very critical in current setup and could be completely eliminated from the project by small modifications.
