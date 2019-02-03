package com.helloingob.shopify;

import java.util.List;
import java.util.Map;
import org.apache.commons.text.WordUtils;

import com.helloingob.shopify.data.ProductService;
import com.helloingob.shopify.data.beans.Order;
import com.helloingob.shopify.data.beans.Product;
import com.helloingob.shopify.data.beans.Shop;
import com.helloingob.shopify.data.offer.Offer;
import com.helloingob.shopify.data.offer.OfferHandler;
import com.helloingob.shopify.util.ShopComparator;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
@HtmlImport("styles/shared-styles.html")
@Route("")
@Viewport("initial-scale=1, maximum-scale=1")
public class OrderView extends VerticalLayout {

    private ProductService service = ProductService.getInstance();
    private Grid<Order> gridOrders = new Grid<>();
    private TextField textFieldAddProduct = new TextField();

    public OrderView() {
        textFieldAddProduct.setPlaceholder("Add product to shopping list ...");
        textFieldAddProduct.setWidth("100%");
        textFieldAddProduct.setMaxLength(99);
        textFieldAddProduct.addListener(KeyPressEvent.class, new ComponentEventListener<KeyPressEvent>() {
            @Override
            public void onComponentEvent(KeyPressEvent keyPressEvent) {
                if ("Enter".equals(keyPressEvent.getKey())) {
                    addProduct();
                }
            }
        });

        Button buttonAddText = new Button(new Icon(VaadinIcons.PLUS));
        buttonAddText.addClickListener(e -> addProduct());

        Button buttonSwitchContext = new Button(new Icon(VaadinIcons.CROSSHAIRS));
        buttonSwitchContext.addClickListener(e -> buttonSwitchContext.getUI().ifPresent(ui -> ui.navigate("wl")));

        HorizontalLayout filtering = new HorizontalLayout(textFieldAddProduct, buttonAddText, buttonSwitchContext);
        filtering.setWidth("99%");

        List<Shop> shops = service.getShops();

        gridOrders.addColumn(getAmountElement()).setFlexGrow(0).setWidth("150px");
        gridOrders.addColumn(order -> order.getAmount()).setHeader("Amount").setFlexGrow(0).setWidth("80px");
        gridOrders.addColumn(order -> order.getProduct().getTitle()).setHeader("Product");
        Column columnOffer = (Column) gridOrders.addColumn(getOfferElement(OfferHandler.getInstance())).setHeader("Offer");
        columnOffer.setFlexGrow(0).setWidth("255px");
        Column columnShop = (Column) gridOrders.addColumn(getShopElement(shops)).setHeader("Shop");
        columnShop.setFlexGrow(0).setWidth("210px");
        columnShop.setComparator(new ShopComparator());
        gridOrders.addColumn(getRemoveElement()).setFlexGrow(0).setWidth("70px");

        gridOrders.setSizeFull();

        add(filtering, gridOrders);
        setHeight("95%");
        updateList();
    }

    private ComponentRenderer getAmountElement() {
        return new ComponentRenderer<>(item -> {
            Order selectedOrder = (Order) item;
            ListDataProvider<Order> dataProvider = (ListDataProvider<Order>) gridOrders.getDataProvider();

            Button incAmount = new Button(new Icon(VaadinIcons.PLUS), event -> {
                selectedOrder.setAmount(selectedOrder.getAmount() + 1);
                service.updateOrder(selectedOrder);
                dataProvider.refreshAll();
            });

            Button decAmount = new Button(new Icon(VaadinIcons.MINUS), event -> {
                if (selectedOrder.getAmount() > 1) {
                    selectedOrder.setAmount(selectedOrder.getAmount() - 1);
                    service.updateOrder(selectedOrder);
                    dataProvider.refreshAll();
                }
            });
            return new HorizontalLayout(incAmount, decAmount);
        });
    }

    private ComponentRenderer getOfferElement(OfferHandler offerHandler) {
        return new ComponentRenderer<>(item -> {
            Order selectedOrder = (Order) item;

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Product product = selectedOrder.getProduct();
            for (Map.Entry<com.helloingob.shopify.data.offer.Shop, List<Offer>> entry : offerHandler.getOfferedShops(product).entrySet()) {
                com.helloingob.shopify.data.offer.Shop shop = entry.getKey();
                Image image = new Image(VaadinServlet.getCurrent().getServletContext().getContextPath() + shop.getImageSrc(), shop.toString());
                image.addListener(ClickEvent.class, new ComponentEventListener<ClickEvent>() {
                    @Override
                    public void onComponentEvent(ClickEvent clickEvent) {
                        UI.getCurrent().getPage().executeJavaScript("window.open(\"" + entry.getValue().get(0).getLink() + "\",'_blank');");
                    }
                });
                horizontalLayout.add(image);
            }
            return horizontalLayout;
        });
    }

    private ComponentRenderer getShopElement(List<Shop> shops) {
        return new ComponentRenderer<>(item -> {
            Order selectedOrder = (Order) item;
            ComboBox<Shop> comboBoxShop = new ComboBox<>();
            comboBoxShop.setItems(shops);
            comboBoxShop.setPlaceholder("Shop?");
            comboBoxShop.setPreventInvalidInput(true);
            comboBoxShop.setItemLabelGenerator(shop -> shop.getTitle());

            if (selectedOrder.getShop() != null) {
                for (Shop shop : shops) {
                    if (shop.getTitle().equals(selectedOrder.getShop().getTitle())) {
                        comboBoxShop.setValue(shop);
                        break;
                    }
                }
            }

            comboBoxShop.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    selectedOrder.setShop(event.getValue());
                } else {
                    selectedOrder.setShop(null);
                }
                service.updateOrder(selectedOrder);
            });
            return comboBoxShop;
        });
    }

    private ComponentRenderer getRemoveElement() {
        return new ComponentRenderer<>(item -> {
            Order selectedOrder = (Order) item;

            Button buttonRemove = new Button(new Icon(VaadinIcons.CLOSE_BIG), event -> {
                service.deleteOrder(selectedOrder.getId());
                ListDataProvider<Order> dataProvider = (ListDataProvider<Order>) gridOrders.getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();
            });
            return buttonRemove;
        });
    }

    public void updateList() {
        gridOrders.setItems(service.getOrders());
    }

    public void addProduct() {
        String userInput = textFieldAddProduct.getValue();
        if (userInput.length() < 100) {
            Order order = new Order();
            order.setProduct(service.addProduct(WordUtils.capitalize(userInput)));
            service.addOrder(order);
            textFieldAddProduct.clear();
            updateList();
        }
    }

}
