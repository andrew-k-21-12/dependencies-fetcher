This is a sandbox project integrating the dependencies fetcher plugin. 
It demonstrates how to include the plugin, allows to debug and test it, 
check its bundling and publication to the Gradle Plugin Portal.


## Integration modes

The plugin can be integrated into this sandbox with 3 possible ways.

1. By default (no actions are needed to switch to this mode) the plugin is included directly by its sources.
   This way is helpful for debugging and testing as all updates to the sources become immediately available.

2. To include the plugin in the form of the compiled JAR artifact (to test its bundling), 
   make sure the **library** module is built (see the corresponding docs inside of it) 
   and its compiled JAR is located in the default output directory (just don't remove or move it anywhere). 
   Create a `local.properties` file if it doesn't exist in this folder, 
   add the following property into it and sync Gradle:
   ```
   includeFetcherVia=artifact
   ```

3. To grab the plugin from the Gradle Plugin Portal (to check its publication),
   add the following property into the `local.properties` file and sync Gradle:
   ```
   includeFetcherVia=portal
   ```
