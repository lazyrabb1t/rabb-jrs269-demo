package xyz.lazyrabbit.util;

import java.util.List;

/**
 * 方法对象
 */
public class MethodDTO {
    @Override
    public String toString() {
        return "MethodDTO{" +
                "methodName='" + methodName + '\'' +
                ", returnType='" + returnType + '\'' +
                ", variableType='" + variableType + '\'' +
                ", variableName='" + variableName + '\'' +
                ", returnFieldList=" + returnFieldList +
                '}';
    }

    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 返回值类型
     */
    private String returnType;
    /**
     * 参数类型
     */
    private String variableType;
    /**
     * 参数名称
     */
    private String variableName;
    /**
     * 返回值字段名称列表
     */
    private List<String> returnFieldList;

    public MethodDTO() {
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public List<String> getReturnFieldList() {
        return returnFieldList;
    }

    public void setReturnFieldList(List<String> returnFieldList) {
        this.returnFieldList = returnFieldList;
    }

    public MethodDTO(String returnType, String variableType, String variableName, List<String> returnFieldList) {
        this.returnType = returnType;
        this.variableType = variableType;
        this.variableName = variableName;
        this.returnFieldList = returnFieldList;
    }
}
