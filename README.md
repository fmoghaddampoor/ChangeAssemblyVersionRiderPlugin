Assembly Version Manager (Rider plugin)

Overview
- Adds a context menu item in the Project View called "Assembly Version…"
- Clicking it opens a dialog where you can set Assembly and File versions by parts (Major.Minor.Build.Revision), similar to Visual Studio.
- Applies to all `.csproj` files within the selected node or the whole project if nothing specific is selected.

Build and Run
1. Install Java 17 (JDK).
2. From the project root, run:
```
./gradlew runIde    # macOS/Linux
# or
gradlew.bat runIde  # Windows
```
This downloads Rider and starts it with the plugin enabled.

Usage
1. Open a .NET solution in the sandboxed Rider instance.
2. Right‑click the project or a folder in the Project View.
3. Choose "Assembly Version…".
4. Enter version parts for Assembly and File version (or keep them synchronized), then press OK.
5. The plugin updates `<Version>`, `<AssemblyVersion>`, and `<FileVersion>` in a `<PropertyGroup>` of each affected `.csproj`.

Notes and limitations
- Wildcards `*` are not supported; enter explicit numbers 0–65535 for each part.
- The action is visible only when at least one `.csproj` is found under the selection.
- Versions are written into the first `<PropertyGroup>` (created if missing).
