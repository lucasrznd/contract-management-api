package br.com.lucasrznd.contractmanagementapi.services;

import body.doc.DeParaTemplate;
import body.doc.DocFromTemplate;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import docs.DocRequests;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import response.DocResponse;
import services.JsonConverter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.utils.StringUtils.*;

@Service
public class ZapSignService {

    @Value("${zapsign.token}")
    private String apiToken;

    @Value("${zapsign.template.token.cnpj}")
    private String cnpjTemplate;

    public DocResponse generateDocument(Contract contract) {
        try {
            DocResponse docResponse = new DocRequests(apiToken).createDocFromTemplate(fillDocTemplate(contract));
            return docResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DocResponse getDocByToken(String token) {
        try {
            DocResponse docResponse = new DocRequests(apiToken).detailDoc(token);
            return docResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DocFromTemplate fillDocTemplate(Contract contract) {
        DocFromTemplate docFromTemplate = DocFromTemplate.docFromTemplateBuilder()
                .sandbox(true)
                .lang("pt-br")
                .name(signerName(contract))
                .signer_name(signerName(contract))
                .template_id(cnpjTemplate)
                .data(generateTemplateVariables(contract))
                .build();

        return docFromTemplate;
    }

    private List<DeParaTemplate> generateTemplateVariables(Contract contract) {
        DeParaTemplate advertisingOrder = DeParaTemplate.deParaTemplateBuilder()
                .de("ORD").para(contract.getAdvertisingOrder().toString()).build();

        DeParaTemplate businessName = DeParaTemplate.deParaTemplateBuilder()
                .de("RAZAO_SOCIAL").para(contract.getClientCompany().getBusinessName().toUpperCase()).build();

        DeParaTemplate tradeName = DeParaTemplate.deParaTemplateBuilder()
                .de("NOME_FANTASIA").para(contract.getClientCompany().getTradeName().toUpperCase()).build();

        DeParaTemplate registrationNumber = DeParaTemplate.deParaTemplateBuilder()
                .de("CNPJ").para(contract.getClientCompany().getRegistrationNumber()).build();

        DeParaTemplate stateRegistration = DeParaTemplate.deParaTemplateBuilder()
                .de("INSC_ESTADUAL").para(contract.getClientCompany().getStateRegistration()).build();

        DeParaTemplate address = DeParaTemplate.deParaTemplateBuilder()
                .de("ENDERECO").para(contract.getClientCompany().getFullAddressWithArea()).build();

        DeParaTemplate phoneNumber = DeParaTemplate.deParaTemplateBuilder()
                .de("TELEFONE").para(formatPhoneNumber(contract.getClientCompany().getPhoneNumber())).build();

        DeParaTemplate email = DeParaTemplate.deParaTemplateBuilder()
                .de("EMAIL").para(contract.getClientCompany().getEmail()).build();

        DeParaTemplate quantitySpotDay = DeParaTemplate.deParaTemplateBuilder()
                .de("QSPT").para(String.valueOf(contract.getQuantitySpotDay())).build();

        DeParaTemplate spotDuration = DeParaTemplate.deParaTemplateBuilder()
                .de("DSPT").para(String.format("%.2f", contract.getSpotDuration())).build();

        DeParaTemplate startDate = DeParaTemplate.deParaTemplateBuilder()
                .de("DTINI").para(formatDateToLocal(contract.getStartDate())).build();

        DeParaTemplate endDate = DeParaTemplate.deParaTemplateBuilder()
                .de("DTTE").para(formatDateToLocal(contract.getEndDate())).build();

        DeParaTemplate testimonialQuantity = DeParaTemplate.deParaTemplateBuilder()
                .de("QTES").para(String.valueOf(contract.getTestimonialQuantity())).build();

        DeParaTemplate testimonialDuration = DeParaTemplate.deParaTemplateBuilder()
                .de("DTES").para(String.format("%.2f", contract.getTestimonialDuration())).build();

        DeParaTemplate monthlyPrice = DeParaTemplate.deParaTemplateBuilder()
                .de("PRECO_MENSAL").para(NumberFormat.getCurrencyInstance().format(contract.getMonthlyPrice())).build();

        DeParaTemplate flashQuantity = DeParaTemplate.deParaTemplateBuilder()
                .de("QTD_FLASH").para(String.valueOf(contract.getFlashQuantity())).build();

        DeParaTemplate newspaperParticipation = DeParaTemplate.deParaTemplateBuilder()
                .de("PARTICIPACAO_JORNAL").para(contract.getNewspaperParticipation().toUpperCase()).build();

        DeParaTemplate paymentMethod = DeParaTemplate.deParaTemplateBuilder()
                .de("FORMA_PAGAMENTO").para(contract.getPaymentMethod().getDescription()).build();

        DeParaTemplate paymentDueDay = DeParaTemplate.deParaTemplateBuilder()
                .de("DIA_VENCIMENTO").para(contract.getPaymentDueDay().toString()).build();

        DeParaTemplate observation = DeParaTemplate.deParaTemplateBuilder()
                .de("OBSERVACOES").para(contract.getObservation()).build();

        DeParaTemplate footerDate = DeParaTemplate.deParaTemplateBuilder()
                .de("DATA_RODAPE").para(currentDate()).build();

        List<DeParaTemplate> templates = new ArrayList<>();
        templates.add(advertisingOrder);
        templates.add(businessName);
        templates.add(tradeName);
        templates.add(registrationNumber);
        templates.add(stateRegistration);
        templates.add(address);
        templates.add(phoneNumber);
        templates.add(email);
        templates.add(quantitySpotDay);
        templates.add(startDate);
        templates.add(endDate);
        templates.add(testimonialQuantity);
        templates.add(monthlyPrice);
        templates.add(spotDuration);
        templates.add(flashQuantity);
        templates.add(testimonialDuration);
        templates.add(newspaperParticipation);
        templates.add(paymentMethod);
        templates.add(paymentDueDay);
        templates.add(observation);
        templates.add(footerDate);

        return templates;
    }

    private String signerName(Contract contract) {
        return contract.getClientCompany().getBusinessName().toUpperCase();
    }

}
