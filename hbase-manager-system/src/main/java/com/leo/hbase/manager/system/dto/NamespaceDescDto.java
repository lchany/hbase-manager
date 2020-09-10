package com.leo.hbase.manager.system.dto;

import com.google.common.base.Converter;
import com.leo.hbase.manager.adaptor.model.NamespaceDesc;
import com.leo.hbase.manager.common.annotation.Excel;
import com.leo.hbase.manager.system.domain.SysHbaseTable;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leojie 2020/9/10 9:52 下午
 */
public class NamespaceDescDto {
    private String namespaceId;
    @Excel(name = "namespace")
    private String namespaceName;
    private List<Property> propertyList;

    public NamespaceDesc convertTo() {
        NamespaceDescDto.NamespaceDescDtoConvert convert = new NamespaceDescDto.NamespaceDescDtoConvert();
        return convert.convert(this);
    }

    public NamespaceDescDto convertFot(NamespaceDesc namespaceDesc) {
        NamespaceDescDto.NamespaceDescDtoConvert convert = new NamespaceDescDto.NamespaceDescDtoConvert();
        return convert.reverse().convert(namespaceDesc);
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    @NotBlank(message = "HBase命名空间不能为空")
    @Size(min = 1, max = 128, message = "HBase命名空间长度不能超过128个字符")
    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public static class NamespaceDescDtoConvert extends Converter<NamespaceDescDto, NamespaceDesc> {

        @Override
        protected NamespaceDesc doForward(NamespaceDescDto namespaceDescDto) {
            NamespaceDesc namespaceDesc = new NamespaceDesc();
            BeanUtils.copyProperties(namespaceDescDto, namespaceDesc);
            if (namespaceDescDto.getPropertyList() != null && !namespaceDescDto.getPropertyList().isEmpty()) {
                Map<String, String> props = new HashMap<>(namespaceDescDto.getPropertyList().size());
                namespaceDescDto.getPropertyList().forEach(property -> {
                    props.put(property.getKey(), property.getValue());
                });
                namespaceDesc.setNamespaceProps(props);
            }
            return namespaceDesc;
        }

        @Override
        protected NamespaceDescDto doBackward(NamespaceDesc namespaceDesc) {
            NamespaceDescDto namespaceDescDto = new NamespaceDescDto();
            BeanUtils.copyProperties(namespaceDesc, namespaceDescDto);
            if (namespaceDesc.getNamespaceProps() != null && !namespaceDesc.getNamespaceProps().isEmpty()) {
                List<Property> properties = new ArrayList<>(namespaceDesc.getNamespaceProps().size());
                namespaceDesc.getNamespaceProps().forEach((k, v) -> {
                    Property property = new Property();
                    property.setKey(k);
                    property.setValue(v);
                    properties.add(property);
                });
                namespaceDescDto.setPropertyList(properties);
            }
            return namespaceDescDto;
        }
    }

    @Override
    public String toString() {
        return "NamespaceDescDto{" +
                "namespaceId='" + namespaceId + '\'' +
                ", namespaceName='" + namespaceName + '\'' +
                ", propertyList=" + propertyList +
                '}';
    }
}
