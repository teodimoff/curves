import sbtcrossproject.CrossPlugin.autoImport.crossProject

val sharedSettings = Seq(scalaVersion := "2.13.4")


lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(sharedSettings)

lazy val draw = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(sharedSettings)
  .dependsOn(core)


val nativeSettings = Seq(
  nativeLinkStubs := true,
  nativeMode in Compile := "release-fast",
  nativeMode in Test := "debug",
  nativeLTO := "thin"
)


def example(project: sbtcrossproject.CrossProject.Builder, exampleName: String) = {
  project
    .in(file(s"example/${exampleName}"))
    .dependsOn(core,draw)
    .settings(sharedSettings)
    .nativeSettings(nativeSettings)
}

lazy val hashdraw =
  example(crossProject(NativePlatform), "hashdraw")

