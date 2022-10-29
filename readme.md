# ∆ PLSAR

PLSAR is an Open Source Server + Framework Environment for large scale requirements. 
It is a single runnable server via Gradle. 
There are no static references. Every request has its own thread, so nothing is blocked. 
No file reads, no object instantiations, no reflection, no access to static fields per request, 
everything is cached, unless specified for development purposes.Consider it like Node.js 
for the JVM on modded out for large scale request requirements, millions of concurrent connections.

We even perform a lightweight dependency injection routine so you are not stuck boiler-plating. 
We followed Spring so everything should be familiar.




25% spring 3% gitbook 40% jetbrains 15% H2