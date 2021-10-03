package com.wasacz.hfms.finance.transaction.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.shop.ShopResponse;
import com.wasacz.hfms.finance.transaction.expense.receiptFile.FileReceiptResponse;
import com.wasacz.hfms.helpers.CategoryCreatorStatic;
import com.wasacz.hfms.helpers.CurrentUserMock;
import com.wasacz.hfms.helpers.FileToMultipartFileConverter;
import com.wasacz.hfms.helpers.ShopCreatorStatic;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.security.UserPrincipal;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseTransactionControllerIntegrationTest {

    @Value("${app.receipt.storage.path}")
    private String destinationPath;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    private ShopResponse shopResponse;

    private ExpenseCategoryResponse expenseCategoryResponse;

    @BeforeAll
    public void setup() throws Exception {
        File directory = new File(destinationPath);
        if(directory.exists()) {
            FileUtils.cleanDirectory(directory);
        }
        currentUser = currentUserMock.createMockUser("User_expense_2", Role.ROLE_USER);
        MvcResult shop = ShopCreatorStatic.callCreateShopEndpoint(mockMvc, "existing_shop_2", currentUser);
        shopResponse = objectMapper.readValue(shop.getResponse().getContentAsString(), ShopResponse.class);
        MvcResult category = CategoryCreatorStatic.callCreateExpenseCategoryEndpoint(mockMvc, "categoryName_2", currentUser);
        expenseCategoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
    }

    @Test
    public void whenGetReceiptFile_givenTransactionId_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_05_09")
                .amount(129.99)
                .transactionDate(LocalDate.now())
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg"))
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        MvcResult fileResult = this.mockMvc.perform(get("/api/transaction/expense/" + expenseResponse.getId() + "/file").with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        FileReceiptResponse fileResponse = objectMapper.readValue(fileResult.getResponse().getContentAsString(), FileReceiptResponse.class);

        assertTrue(fileResponse.getLength() > 0);
        assertTrue(fileResponse.getName().contains("expense_2021_05_09"));
        assertFalse(fileResponse.getBase64Resource().isEmpty());
    }

    @Test
    public void whenGetReceiptFile_givenIncorrectTransactionId_thenReturnBadRequestStatus() throws Exception {
        this.mockMvc.perform(get("/api/transaction/expense/" + 9999 + "/file").with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Transaction 9999 not found."));
    }

    @Test
    public void whenDeleteReceiptFile_givenTransactionId_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_05_09_2")
                .amount(129.99)
                .transactionDate(LocalDate.now())
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg"))
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        MvcResult mvcResult = this.mockMvc.perform(delete("/api/transaction/expense/" + expenseResponse.getId() + "/file").with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("File has been deleted.",mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void whenDeleteReceiptFile_givenIncorrectTransactionId_thenReturnBadRequestStatus() throws Exception {
        this.mockMvc.perform(delete("/api/transaction/expense/" + 9999 + "/file").with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Transaction 9999 not found."));
    }

    @Test
    public void whenUploadReceiptFile_givenExpenseIdAndFile_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_05_09_3")
                .amount(129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        assertNull(expenseResponse.getReceiptId());

        MvcResult fileResult = this.mockMvc.perform(multipart("/api/transaction/expense/" + expenseResponse.getId() + "/file")
                .file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg"))
                .with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        FileReceiptResponse fileResponse = objectMapper.readValue(fileResult.getResponse().getContentAsString(), FileReceiptResponse.class);

        assertTrue(fileResponse.getLength() > 0);
        assertTrue(fileResponse.getName().contains("expense_2021_05_09_3"));
        assertFalse(fileResponse.getBase64Resource().isEmpty());
    }

    @Test
    public void whenUploadReceiptFile_givenIncorrectTransactionId_thenReturnBadRequestStatus() throws Exception {
        this.mockMvc.perform(multipart("/api/transaction/expense/" + 9999 + "/file")
                .file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg"))
                .with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Transaction 9999 not found."));
    }


}