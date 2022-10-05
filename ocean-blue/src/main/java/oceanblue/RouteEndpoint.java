package oceanblue;

import oceanblue.model.TypeAttributes;
import oceanblue.model.UrlBitFeatures;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RouteEndpoint {
    String routePath;
    String regexRoutePath;
    String routeVerb;

    Method routeMethod;

    List<TypeAttributes> typeDetails;
    List<String> typeNames;
    List<Integer> variablePositions;

    Class<?> klass;

    UrlBitFeatures urlBitFeatures;

    public RouteEndpoint(){
        this.typeNames = new ArrayList<>();
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

    public List<TypeAttributes> getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(List<TypeAttributes> typeDetails) {
        this.typeDetails = typeDetails;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<Integer> getVariablePositions() {
        return variablePositions;
    }

    public void setVariablePositions(List<Integer> variablePositions) {
        this.variablePositions = variablePositions;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public UrlBitFeatures getUrlBitFeatures() {
        return urlBitFeatures;
    }

    public void setUrlBitFeatures(UrlBitFeatures urlBitFeatures) {
        this.urlBitFeatures = urlBitFeatures;
    }
}
