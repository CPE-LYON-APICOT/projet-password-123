package fr.cpe.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PasswordItem.class, name = "password"),
    @JsonSubTypes.Type(value = CreditCardItem.class, name = "creditcard"),
    @JsonSubTypes.Type(value = SshKeyItem.class, name = "sshkey")
})
public interface IVaultItem {
    String getName();
    String getDescription();
}
