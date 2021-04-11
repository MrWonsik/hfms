package com.wasacz.hfms.finance.category.income;

import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.finance.category.ICategoryManagementService;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Service
public class IncomeCategoryManagementService implements ICategoryManagementService {

    private final IncomeCategoryRepository incomeCategoryRepository;

    public IncomeCategoryManagementService(IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
    }

    @Override
    public IncomeCategoryResponse addCategory(CreateCategoryRequest categoryRequest, User user) {
        IncomeCategoryObj incomeCategoryObj = getIncomeCategoryObj(categoryRequest);
        CategoryValidator.validate(incomeCategoryObj);
        IncomeCategory incomeCategoryPersistence = buildIncomeCategory(incomeCategoryObj, user);
        IncomeCategory savedIncomeCategory = incomeCategoryRepository.save(incomeCategoryPersistence);
        return getIncomeCategoryResponse(savedIncomeCategory);
    }

    private IncomeCategoryObj getIncomeCategoryObj(CreateCategoryRequest request) {
        return IncomeCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .build();
    }

    @Override
    public IncomeCategoryResponse setAsFavourite(long categoryId, boolean isFavourite, User user) {
        IncomeCategory incomeCategoryToUpdate = incomeCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Income category not found."));
        incomeCategoryToUpdate.setIsFavourite(isFavourite);
        IncomeCategory updatedExpenseCategory = incomeCategoryRepository.save(incomeCategoryToUpdate);
        return getIncomeCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public IncomeCategoryResponse deleteCategory(long expenseCategoryId, User user) {
        IncomeCategory incomeCategory = incomeCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Income category not found."));
        incomeCategory.setIsDeleted(true);
        IncomeCategory updatedExpenseCategory = incomeCategoryRepository.save(incomeCategory);
        return getIncomeCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public CategoriesResponse getAllCategories(User user) {
        List<IncomeCategory> incomeCategories = incomeCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new CategoriesResponse(incomeCategories.stream().map(this::getIncomeCategoryResponse).collect(Collectors.toList()));
    }

    private IncomeCategory buildIncomeCategory(IncomeCategoryObj incomeCategoryObj, User user) {
        return IncomeCategory.builder()
                .categoryName(incomeCategoryObj.getCategoryName())
                .colorHex(incomeCategoryObj.getColorHex() != null ? incomeCategoryObj.getColorHex() : getRandomHexColor())
                .isFavourite(incomeCategoryObj.getIsFavourite() != null ? incomeCategoryObj.getIsFavourite() : false)
                .user(user)
                .build();
    }

    private IncomeCategoryResponse getIncomeCategoryResponse(IncomeCategory incomeCategory) {
        return IncomeCategoryResponse.builder()
                .id(incomeCategory.getId())
                .categoryName(incomeCategory.getCategoryName())
                .colorHex(incomeCategory.getColorHex())
                .isDeleted(incomeCategory.getIsDeleted())
                .isFavourite(incomeCategory.getIsFavourite())
                .createDate(new DateTime(incomeCategory.getCreatedDate()))
                .build();
    }
}
