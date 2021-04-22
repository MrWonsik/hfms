package com.wasacz.hfms.finance.category.income;

import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.finance.category.controller.CategoryObj;
import com.wasacz.hfms.finance.category.ICategoryManagementService;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Service
public class IncomeCategoryService implements ICategoryManagementService {

    private final IncomeCategoryRepository incomeCategoryRepository;

    public IncomeCategoryService(IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
    }

    @Override
    public IncomeCategoryResponse addCategory(CategoryObj categoryRequest, User user) {
        IncomeCategoryObj incomeCategoryObj = getIncomeCategoryObj(categoryRequest);
        CategoryValidator.validate(incomeCategoryObj);
        IncomeCategory incomeCategoryPersistence = buildIncomeCategory(incomeCategoryObj, user);
        IncomeCategory savedIncomeCategory = incomeCategoryRepository.save(incomeCategoryPersistence);
        return mapIncomeCategoryResponse(savedIncomeCategory);
    }

    private IncomeCategoryObj getIncomeCategoryObj(CategoryObj request) {
        return IncomeCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .build();
    }

    @Override
    public IncomeCategoryResponse setAsFavourite(long categoryId, boolean isFavourite, User user) {
        IncomeCategory incomeCategoryToUpdate = findByIdAndUser(categoryId, user);
        incomeCategoryToUpdate.setIsFavourite(isFavourite);
        IncomeCategory updatedExpenseCategory = incomeCategoryRepository.save(incomeCategoryToUpdate);
        return mapIncomeCategoryResponse(updatedExpenseCategory);
    }

    private IncomeCategory findByIdAndUser(long categoryId, User user) {
        return incomeCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Income category not found."));
    }

    @Override
    public IncomeCategoryResponse deleteCategory(long expenseCategoryId, User user) {
        IncomeCategory incomeCategory = findByIdAndUser(expenseCategoryId, user);
        incomeCategory.setIsDeleted(true);
        IncomeCategory updatedExpenseCategory = incomeCategoryRepository.save(incomeCategory);
        return mapIncomeCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public CategoriesResponse getAllCategories(User user) {
        List<IncomeCategory> incomeCategories = incomeCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new CategoriesResponse(incomeCategories.stream().map(this::mapIncomeCategoryResponse).collect(Collectors.toList()));
    }

    @Override
    public IncomeCategoryResponse editCategory(long id, String newCategoryName, String newColorHex, User user) {
        CategoryValidator.validateBeforeEdit(newCategoryName, newColorHex);
        IncomeCategory incomeCategory = findByIdAndUser(id, user);
        if(newColorHex == null && newCategoryName == null) {
            return mapIncomeCategoryResponse(incomeCategory);
        }

        if(newColorHex != null) {
            incomeCategory.setColorHex(newColorHex);
        }
        if(newCategoryName != null) {
            incomeCategory.setCategoryName(newCategoryName);
        }
        IncomeCategory updatedIncomeCategory = incomeCategoryRepository.save(incomeCategory);
        return mapIncomeCategoryResponse(updatedIncomeCategory);
    }

    private IncomeCategory buildIncomeCategory(IncomeCategoryObj incomeCategoryObj, User user) {
        return IncomeCategory.builder()
                .categoryName(incomeCategoryObj.getCategoryName())
                .colorHex(incomeCategoryObj.getColorHex() != null ? incomeCategoryObj.getColorHex() : getRandomHexColor())
                .isFavourite(incomeCategoryObj.getIsFavourite() != null ? incomeCategoryObj.getIsFavourite() : false)
                .user(user)
                .build();
    }

    private IncomeCategoryResponse mapIncomeCategoryResponse(IncomeCategory incomeCategory) {
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