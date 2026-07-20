package com.smart.ecommerce.dto.address;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.dto.address.AddressDtos.CreateAddressRequest;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AddressDtosValidationTest {
 @Test void rejectsInvalidLatitude(){ try(var factory=Validation.buildDefaultValidatorFactory()){ var validator=factory.getValidator(); var request=new CreateAddressRequest("Naser","079","Amman",new BigDecimal("91.0"),new BigDecimal("35.0"),null,null,null); assertThat(validator.validate(request)).anyMatch(v->v.getPropertyPath().toString().equals("latitude")); } }
 @Test void rejectsInvalidLongitude(){ try(var factory=Validation.buildDefaultValidatorFactory()){ var validator=factory.getValidator(); var request=new CreateAddressRequest("Naser","079","Amman",new BigDecimal("31.0"),new BigDecimal("181.0"),null,null,null); assertThat(validator.validate(request)).anyMatch(v->v.getPropertyPath().toString().equals("longitude")); } }
}
