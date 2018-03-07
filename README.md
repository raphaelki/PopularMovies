# PopularMovies

Android app which retrieves and shows popular movies from https://www.themoviedb.org.

To build the app you have to provide an API key for TMDB as a string ressource with the name attribute "api_key".

You can create an additional xml file, for example:
`app\src\main\res\values\api_key.xml`
containing
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string  name="api_key" translatable="false">PUT_API_KEY_HERE</string>
</resources>
```
