package org.evomaster.client.java.controller.problem.rpc;

import org.evomaster.client.java.controller.problem.rpc.schema.EndpointSchema;
import org.evomaster.client.java.controller.api.dto.problem.rpc.schema.dto.ParamDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.schema.dto.RPCActionDto;
import org.evomaster.client.java.controller.api.dto.problem.rpc.schema.dto.RPCSupportedDataType;
import org.evomaster.client.java.controller.problem.rpc.schema.params.*;
import org.evomaster.client.java.controller.problem.rpc.schema.types.*;
import org.evomaster.client.java.controller.problem.RPCType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * created by manzhang on 2021/11/12
 */
public class ExampleBuilderTest extends RPCEndpointsBuilderTestBase {

    @Override
    public String getInterfaceName() {
        return "com.thrift.example.artificial.RPCInterfaceExample";
    }

    @Override
    public int expectedNumberOfEndpoints() {
        return 8;
    }

    @Override
    public RPCType getRPCType() {
        return RPCType.GENERAL;
    }

    @Test
    public void testEndpointsLoad(){
        assertEquals(expectedNumberOfEndpoints(), schema.getEndpoints().size());
    }

    @Test
    public void testSimplePrimitive(){

        EndpointSchema endpoint = getOneEndpoint("simplePrimitive");
        assertNotNull(endpoint.getResponse());
        assertEquals(8, endpoint.getRequestParams().size());
        assertTrue(endpoint.getRequestParams().get(0) instanceof IntParam);
        assertTrue(endpoint.getRequestParams().get(1) instanceof FloatParam);
        assertTrue(endpoint.getRequestParams().get(2) instanceof LongParam);
        assertTrue(endpoint.getRequestParams().get(3) instanceof DoubleParam);
        assertTrue(endpoint.getRequestParams().get(4) instanceof CharParam);
        assertTrue(endpoint.getRequestParams().get(5) instanceof ByteParam);
        assertTrue(endpoint.getRequestParams().get(6) instanceof BooleanParam);
        assertTrue(endpoint.getRequestParams().get(7) instanceof ShortParam);

    }

    @Test
    public void testSimplePrimitiveToFromDTO() throws ClassNotFoundException {

        EndpointSchema endpoint = getOneEndpoint("simplePrimitive");
        RPCActionDto dto = endpoint.getDto().copy();
        assertEquals(8, dto.requestParams.size());
        dto.requestParams.get(0).jsonValue = ""+42;
        dto.requestParams.get(1).jsonValue = ""+4.2f;
        dto.requestParams.get(2).jsonValue = ""+42L;
        dto.requestParams.get(3).jsonValue = ""+4.2;
        dto.requestParams.get(4).jsonValue = ""+'x';
        dto.requestParams.get(5).jsonValue = ""+ Byte.parseByte("42");
        dto.requestParams.get(6).jsonValue = ""+ false;
        dto.requestParams.get(7).jsonValue = ""+ Short.parseShort("42");
        endpoint.setValue(dto);
        assertEquals(42, endpoint.getRequestParams().get(0).newInstance());
        assertEquals(4.2f, endpoint.getRequestParams().get(1).newInstance());
        assertEquals(42L, endpoint.getRequestParams().get(2).newInstance());
        assertEquals(4.2, endpoint.getRequestParams().get(3).newInstance());
        assertEquals('x', endpoint.getRequestParams().get(4).newInstance());
        assertEquals(Byte.parseByte("42"), endpoint.getRequestParams().get(5).newInstance());
        assertEquals(false, endpoint.getRequestParams().get(6).newInstance());
        assertEquals(Short.parseShort("42"), endpoint.getRequestParams().get(7).newInstance());

    }

    @Test
    public void testSimpleWrapPrimitive(){

        EndpointSchema endpoint = getOneEndpoint("simpleWrapPrimitive");
        assertNotNull(endpoint.getResponse());
        assertEquals(8, endpoint.getRequestParams().size());
        assertTrue(endpoint.getRequestParams().get(0) instanceof IntParam);
        assertTrue(endpoint.getRequestParams().get(1) instanceof FloatParam);
        assertTrue(endpoint.getRequestParams().get(2) instanceof LongParam);
        assertTrue(endpoint.getRequestParams().get(3) instanceof DoubleParam);
        assertTrue(endpoint.getRequestParams().get(4) instanceof CharParam);
        assertTrue(endpoint.getRequestParams().get(5) instanceof ByteParam);
        assertTrue(endpoint.getRequestParams().get(6) instanceof BooleanParam);
        assertTrue(endpoint.getRequestParams().get(7) instanceof ShortParam);

    }

    @Test
    public void testSimpleWrapPrimitiveToFromDTO() throws ClassNotFoundException {

        EndpointSchema endpoint = getOneEndpoint("simpleWrapPrimitive");
        RPCActionDto dto = endpoint.getDto().copy();
        assertEquals(8, dto.requestParams.size());
        dto.requestParams.get(0).jsonValue = ""+42;
        dto.requestParams.get(1).jsonValue = ""+4.2f;
        dto.requestParams.get(2).jsonValue = ""+42L;
        dto.requestParams.get(3).jsonValue = ""+4.2;
        dto.requestParams.get(4).jsonValue = ""+'x';
        dto.requestParams.get(5).jsonValue = ""+ Byte.parseByte("42");
        dto.requestParams.get(6).jsonValue = ""+ false;
        dto.requestParams.get(7).jsonValue = ""+ Short.parseShort("42");
        endpoint.setValue(dto);
        assertEquals(42, endpoint.getRequestParams().get(0).newInstance());
        assertEquals(4.2f, endpoint.getRequestParams().get(1).newInstance());
        assertEquals(42L, endpoint.getRequestParams().get(2).newInstance());
        assertEquals(4.2, endpoint.getRequestParams().get(3).newInstance());
        assertEquals('x', endpoint.getRequestParams().get(4).newInstance());
        assertEquals(Byte.parseByte("42"), endpoint.getRequestParams().get(5).newInstance());
        assertEquals(false, endpoint.getRequestParams().get(6).newInstance());
        assertEquals(Short.parseShort("42"), endpoint.getRequestParams().get(7).newInstance());

    }

    @Test
    public void testArray(){

        EndpointSchema endpoint = getOneEndpoint("array");
        assertNotNull(endpoint.getResponse());
        assertEquals(1, endpoint.getRequestParams().size());
        NamedTypedValue param = endpoint.getRequestParams().get(0);
        assertTrue(param instanceof ArrayParam);
        assertTrue(param.getType() instanceof CollectionType);
        NamedTypedValue template = ((CollectionType) param.getType()).getTemplate();
        assertTrue(template instanceof ListParam);
        assertTrue(template.getType() instanceof CollectionType);
        assertTrue(((CollectionType) template.getType()).getTemplate() instanceof StringParam);

    }

    @Test
    public void testArrayToFromDto() throws ClassNotFoundException {

        EndpointSchema endpoint = getOneEndpoint("array");
        RPCActionDto dto = endpoint.getDto();
        assertEquals(1, dto.requestParams.size());
        ParamDto paramDto = dto.requestParams.get(0);
        assertEquals(RPCSupportedDataType.ARRAY, paramDto.type.type);
        assertNotNull(paramDto.type.example);
        ParamDto paramExampleDto = paramDto.type.example;
        assertEquals(RPCSupportedDataType.LIST, paramExampleDto.type.type);
        assertNotNull(paramExampleDto.type.example);
        ParamDto paramExampleExampleDto = paramExampleDto.type.example;
        assertEquals(RPCSupportedDataType.STRING, paramExampleExampleDto.type.type);

        List<ParamDto> strs = IntStream.range(0, 3).mapToObj(i->{
            ParamDto p = paramExampleExampleDto.copy();
            p.jsonValue = "str_"+ i;
            return p;
        }).collect(Collectors.toList());

        ParamDto iList = paramExampleDto.copy();
        iList.innerContent = strs;

        paramDto.innerContent = Arrays.asList(iList);
        endpoint.setValue(dto);

        Object arg0Instance = endpoint.getRequestParams().get(0).newInstance();
        // need to fix generic type
//        List<String>[] ins = (List<String>[])arg0Instance;
//        assertEquals(1, ins.length);
//        assertEquals(3, ins[0].size());
    }



    @Test
    public void testArrayBoolean(){

        EndpointSchema endpoint = getOneEndpoint("arrayboolean");
        assertNotNull(endpoint.getResponse());
        assertEquals(1, endpoint.getRequestParams().size());
        NamedTypedValue param = endpoint.getRequestParams().get(0);
        assertTrue(param instanceof ArrayParam);
        assertTrue(param.getType() instanceof CollectionType);
        NamedTypedValue template = ((CollectionType) param.getType()).getTemplate();
        assertTrue(template instanceof BooleanParam);
    }

    @Test
    public void testList(){

        EndpointSchema endpoint = getOneEndpoint("list");
        assertNotNull(endpoint.getResponse());
        assertEquals(1, endpoint.getRequestParams().size());
        NamedTypedValue param = endpoint.getRequestParams().get(0);
        assertTrue(param instanceof ListParam);
        assertTrue(param.getType() instanceof CollectionType);
        NamedTypedValue template = ((CollectionType) param.getType()).getTemplate();
        assertTrue(template instanceof StringParam);

    }

    @Test
    public void testMap(){

        EndpointSchema endpoint = getOneEndpoint("map");
        assertNotNull(endpoint.getResponse());
        assertEquals(1, endpoint.getRequestParams().size());
        NamedTypedValue param = endpoint.getRequestParams().get(0);
        assertTrue(param instanceof MapParam);
        assertTrue(param.getType() instanceof MapType);

        NamedTypedValue pairTemplate = ((MapType) param.getType()).getTemplate();
        assertTrue(pairTemplate instanceof PairParam);

        NamedTypedValue ktemplate = ((PairType) pairTemplate.getType()).getFirstTemplate();
        assertTrue(ktemplate instanceof StringParam);

        NamedTypedValue vtemplate = ((PairType) pairTemplate.getType()).getSecondTemplate();
        assertTrue(vtemplate instanceof StringParam);

    }

    @Test
    public void testListAndMap(){

        EndpointSchema endpoint = getOneEndpoint("listAndMap");
        assertNotNull(endpoint.getResponse());
        assertEquals(1, endpoint.getRequestParams().size());
        NamedTypedValue param = endpoint.getRequestParams().get(0);

        assertTrue(param instanceof ListParam);
        assertTrue(param.getType() instanceof CollectionType);
        NamedTypedValue mapTemplate = ((CollectionType) param.getType()).getTemplate();

        assertTrue(mapTemplate instanceof MapParam);
        assertTrue(mapTemplate.getType() instanceof MapType);

        NamedTypedValue pairTemplate = ((MapType) mapTemplate.getType()).getTemplate();
        assertTrue(pairTemplate instanceof PairParam);

        NamedTypedValue ktemplate = ((PairType) pairTemplate.getType()).getFirstTemplate();
        assertTrue(ktemplate instanceof StringParam);

        NamedTypedValue vtemplate = ((PairType) pairTemplate.getType()).getSecondTemplate();
        assertTrue(vtemplate instanceof StringParam);

    }


    @Test
    public void testObjResponse(){

        EndpointSchema endpoint = getOneEndpoint("objResponse");
        assertEquals(0, endpoint.getRequestParams().size());

        assertNotNull(endpoint.getResponse());
        NamedTypedValue param = endpoint.getResponse();
        assertTrue(param instanceof ObjectParam);
        assertTrue(param.getType() instanceof ObjectType);

        List<NamedTypedValue> fs = ((ObjectType) param.getType()).getFields();
        assertEquals(6, fs.size());
        assertTrue(fs.get(0) instanceof StringParam);
        assertTrue(fs.get(1) instanceof IntParam);
        assertTrue(fs.get(2) instanceof DoubleParam);
        assertTrue(fs.get(3) instanceof ObjectParam);
        assertTrue(fs.get(4) instanceof ArrayParam);
        assertTrue(fs.get(5) instanceof ArrayParam);

        ObjectParam fs3 = (ObjectParam) fs.get(3);
        assertTrue(fs3.getType().getFields().get(3).getType() instanceof CycleObjectType);

    }

}
