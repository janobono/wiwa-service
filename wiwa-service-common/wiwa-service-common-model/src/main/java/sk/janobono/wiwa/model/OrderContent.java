package sk.janobono.wiwa.model;

public enum OrderContent {
    MATERIAL_NOT_FOUND, BOARD_NOT_FOUND, EDGE_NOT_FOUND,
    CREATOR, CREATED, ORDER_NUMBER, DELIVERY_DATE, PACKAGE_TYPE,
    CONTACT_INFO, NAME, STREET, ZIP_CODE, CITY, STATE, PHONE, EMAIL, BUSINESS_ID, TAX_ID,
    ORDER_SUMMARY, BOARD_SUMMARY, BOARD_SUMMARY_MATERIAL, BOARD_SUMMARY_NAME, BOARD_SUMMARY_AREA, BOARD_SUMMARY_COUNT, BOARD_SUMMARY_WEIGHT, BOARD_SUMMARY_PRICE, BOARD_SUMMARY_VAT_PRICE,
    EDGE_SUMMARY, EDGE_SUMMARY_NAME, EDGE_SUMMARY_LENGTH, EDGE_SUMMARY_GLUE_LENGTH, EDGE_SUMMARY_WEIGHT, EDGE_SUMMARY_EDGE_PRICE, EDGE_SUMMARY_EDGE_VAT_PRICE, EDGE_SUMMARY_GLUE_PRICE, EDGE_SUMMARY_GLUE_VAT_PRICE,
    GLUE_SUMMARY, GLUE_SUMMARY_AREA, GLUE_SUMMARY_PRICE, GLUE_SUMMARY_VAT_PRICE,
    CUT_SUMMARY, CUT_SUMMARY_THICKNESS, CUT_SUMMARY_AMOUNT, CUT_SUMMARY_PRICE, CUT_SUMMARY_VAT_PRICE,
    TOTAL_SUMMARY, TOTAL_SUMMARY_WEIGHT, TOTAL_SUMMARY_PRICE, TOTAL_SUMMARY_VAT_PRICE,
    PARTS_LIST, PARTS_LIST_NAME, PARTS_LIST_NUMBER, PARTS_LIST_X, PARTS_LIST_Y, PARTS_LIST_QUANTITY, PARTS_LIST_DESCRIPTION, PARTS_LIST_EDGES, PARTS_LIST_CORNERS, PARTS_LIST_BOARDS, PARTS_LIST_POSITION
}
