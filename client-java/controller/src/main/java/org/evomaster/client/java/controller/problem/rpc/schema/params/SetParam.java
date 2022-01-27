package org.evomaster.client.java.controller.problem.rpc.schema.params;

import org.evomaster.client.java.controller.api.dto.problem.rpc.ParamDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.RPCSupportedDataType;
import org.evomaster.client.java.controller.problem.rpc.CodeJavaGenerator;
import org.evomaster.client.java.controller.problem.rpc.schema.types.AccessibleSchema;
import org.evomaster.client.java.controller.problem.rpc.schema.types.CollectionType;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * thrift
 *     HashSet (see https://thrift.apache.org/docs/types#containers)
 */
public class SetParam extends CollectionParam<Set<NamedTypedValue>>{

    public SetParam(String name, CollectionType type, AccessibleSchema accessibleSchema) {
        super(name, type, accessibleSchema);
    }

    @Override
    public Object newInstance() throws ClassNotFoundException {
        if (getValue() == null) return null;
        return getValue().stream().map(v-> {
            try {
                return v.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("ArrayParam: could not create new instance for value:"+v.getType());
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public ParamDto getDto() {
        ParamDto dto = super.getDto();
        dto.type.type = RPCSupportedDataType.SET;
        if (getValue() != null){
            dto.innerContent = getValue().stream().map(s-> s.getDto()).collect(Collectors.toList());
        }
        return dto;
    }

    @Override
    public SetParam copyStructure() {
        return new SetParam(getName(), getType(), accessibleSchema);
    }

    @Override
    public void setValueBasedOnDto(ParamDto dto) {
        if (dto.innerContent!= null && !dto.innerContent.isEmpty()){
            NamedTypedValue t = getType().getTemplate();
            Set<NamedTypedValue> values = dto.innerContent.stream().map(s-> {
                NamedTypedValue v = t.copyStructure();
                v.setValueBasedOnDto(s);
                return v;
            }).collect(Collectors.toSet());
            setValue(values);
        }
    }

    @Override
    protected void setValueBasedOnValidInstance(Object instance) {
        NamedTypedValue t = getType().getTemplate();
        // employ linked hash set to avoid flaky tests
        Set<NamedTypedValue> values = new LinkedHashSet<>();
        for (Object e : (Set) instance){
            NamedTypedValue copy = t.copyStructure();
            copy.setValueBasedOnInstance(e);
            values.add(copy);
        }
        setValue(values);
    }

    @Override
    public List<String> newInstanceWithJava(boolean isDeclaration, boolean doesIncludeName, String variableName, int indent) {
        String fullName = getType().getTypeNameForInstance();
        List<String> codes = new ArrayList<>();
        String var = CodeJavaGenerator.oneLineInstance(isDeclaration, doesIncludeName, fullName, variableName, null);
        CodeJavaGenerator.addCode(codes, var, indent);
        if (getValue() == null) return codes;
        CodeJavaGenerator.addCode(codes, "{", indent);
        // new array
        CodeJavaGenerator.addCode(codes,
                CodeJavaGenerator.setInstance(
                        variableName,
                        CodeJavaGenerator.newSet()), indent+1);
        int index = 0;
        for (NamedTypedValue e: getValue()){
            String eVarName = CodeJavaGenerator.handleVariableName(variableName+"_e_"+index);
            codes.addAll(e.newInstanceWithJava(true, true, eVarName, indent+1));
            CodeJavaGenerator.addCode(codes, variableName+".add("+eVarName+");", indent+1);
            index++;
        }

        CodeJavaGenerator.addCode(codes, "}", indent);
        return codes;
    }

    @Override
    public List<String> newAssertionWithJava(int indent, String responseVarName, int maxAssertionForDataInCollection) {
        List<String> codes = new ArrayList<>();
        if (getValue() == null){
            CodeJavaGenerator.addCode(codes, CodeJavaGenerator.junitAssertNull(responseVarName), indent);
            return codes;
        }
        CodeJavaGenerator.addCode(codes, CodeJavaGenerator.junitAssertEquals(""+getValue().size(), CodeJavaGenerator.withSize(responseVarName)), indent);
        /*
            it is tricky to check values for set since the sequence is not determinate
         */
        return codes;
    }

    @Override
    public String getValueAsJavaString() {
        return null;
    }
}