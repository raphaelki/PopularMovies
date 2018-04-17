# Popular Movies Android App

## Project Overview

Android app that shows popular movies from [The Movie Database](https://www.themoviedb.org).\
This is the 2nd project of the [Android Developer Nanodegree Program](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801).

The app uses the [API provided by TMDB](https://developers.themoviedb.org/3/getting-started/introduction) to retrieve the movie information and stores the data in a local SQLite database. Users can get additional information on the movie in a details view and mark movies as favorites.

## Android concepts used

- **ContentProvider** used as an abstraction layer for the underlying SQLite database
- **RecyclerView** for displaying movie posters
- **IntentService** for REST call to the API
- **CursorLoaders** to retrieve latest data from the ContentProvider
- **Broadcasts** for service to fragment communication
- **Implicit Intents** for launching a browser or the Youtube app to watch a movie trailer

## External libraries used

- **[Retrofit](http://square.github.io/retrofit)** for API calls
- **[Glide](https://github.com/bumptech/glide)** for image manipulation and caching

## Building

First get the code via the git client:
```bash
$ git clone https://github.com/raphaki/PopularMovies.git
```

To build the app you will need to create a free user account at TMDB and get an API key. The key needs to be provided in the `gradle.properties` file in the project root directory before importing the project in Android Studio:
```
TMDB_API_KEY="add api key here"
```
