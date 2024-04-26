package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

@Builder
public record OrderItemSummaryData() {
}

//export interface SummaryBorderItem {
//    borderId: number;
//    length: number;
//    price: number;
//    stickLength: number;
//    stickPrice: number;
//}

//export interface SummaryMaterialItem {
//    materialId: number;
//    itemCount: number;
//    sawLength: number;
//    sawPrice: number;
//    doubleSawLength: number;
//    doubleSawPrice: number;
//    rawArea: number;
//    materialCount: number;
//    materialPrice: number;
//    stickArea: number;
//    stickPrice: number;
//    borderItems: SummaryBorderItem[];
//}

//export interface Summary {
//    materialItems: SummaryMaterialItem[];
//    materialItem: SummaryMaterialItem;
//    borderItems: SummaryBorderItem[];
//    borderItem: SummaryBorderItem;
//    totalPrice: number;
//}
