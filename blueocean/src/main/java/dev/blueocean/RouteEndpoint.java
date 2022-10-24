package dev.blueocean;

import dev.blueocean.model.RouteAttribute;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteEndpoint {
    String routePath;
    String regexRoutePath;
    String routeVerb;

    Method routeMethod;

    Map<Integer, RouteAttribute> routeAttributes;

    Class<?> klass;
    Boolean regex;

    public RouteEndpoint(){
        this.routeAttributes = new HashMap<>();
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public String getRegexRoutePath() {
        return regexRoutePath;
    }

    public void setRegexRoutePath(String regexRoutePath) {
        this.regexRoutePath = regexRoutePath;
    }

    public String getRouteVerb() {
        return routeVerb;
    }

    public void setRouteVerb(String routeVerb) {
        this.routeVerb = routeVerb;
    }

    public Method getRouteMethod() {
        return routeMethod;
    }

    public void setRouteMethod(Method routeMethod) {
        this.routeMethod = routeMethod;
    }

    public Map<Integer, RouteAttribute> getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(Map<Integer, RouteAttribute> routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Boolean isRegex() {
        return regex;
    }

    public void setRegex(Boolean regex) {
        this.regex = regex;
    }
}
