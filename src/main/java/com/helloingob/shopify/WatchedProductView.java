package com.helloingob.shopify;

import java.util.List;
import java.util.Map;
import org.apache.commons.text.WordUtils;

import com.helloingob.shopify.data.ProductService;
import com.helloingob.shopify.data.beans.Order;
import com.helloingob.shopify.data.beans.Product;
import com.helloingob.shopify.data.beans.WatchedProduct;
import com.helloingob.shopify.data.offer.Offer;
import com.helloingob.shopify.data.offer.OfferHandler;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.notification.Notification;
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
@Route("wl")
@Viewport("initial-scale=1, maximum-scale=1")
public class WatchedProductView extends VerticalLayout {

    private ProductService service = ProductService.getInstance();
    private Grid<WatchedProduct> gridWatchedProducts = new Grid<>();
    private TextField textFieldAddProduct = new TextField();

    public WatchedProductView() {
        textFieldAddProduct.setPlaceholder("Add product to watch list ...");
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

        Button buttonSwitchContext = new Button(new Icon(VaadinIcons.CART));
        buttonSwitchContext.addClickListener(e -> buttonSwitchContext.getUI().ifPresent(ui -> ui.navigate("")));

        HorizontalLayout filtering = new HorizontalLayout(textFieldAddProduct, buttonAddText, buttonSwitchContext);
        filtering.setWidth("99%");

        gridWatchedProducts.addColumn(watchedProduct -> watchedProduct.getProduct().getTitle()).setHeader("Product");
        Column columnOffer = (Column) gridWatchedProducts.addColumn(getOfferElement(OfferHandler.getInstance())).setHeader("Offer");
        columnOffer.setFlexGrow(0).setWidth("255px");
        gridWatchedProducts.addColumn(getAddElement()).setFlexGrow(0).setWidth("70px");
        gridWatchedProducts.addColumn(getRemoveElement()).setFlexGrow(0).setWidth("70px");

        gridWatchedProducts.setSizeFull();

        add(filtering, gridWatchedProducts);
        setHeight("95%");
        updateList();
    }

    private ComponentRenderer getOfferElement(OfferHandler offerHandler) {
        return new ComponentRenderer<>(item -> {
            WatchedProduct selectedWatchedProduct = (WatchedProduct) item;

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Product product = selectedWatchedProduct.getProduct();
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

    private ComponentRenderer getRemoveElement() {
        return new ComponentRenderer<>(item -> {
            WatchedProduct selectedWatchedProduct = (WatchedProduct) item;

            Button buttonRemove = new Button(new Icon(VaadinIcons.CLOSE_BIG), event -> {
                service.deleteWatchedProduct(selectedWatchedProduct.getId());
                ListDataProvider<WatchedProduct> dataProvider = (ListDataProvider<WatchedProduct>) gridWatchedProducts.getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();
                Notification.show("Product removed from watch list.", 3000, Notification.Position.MIDDLE);
            });
            return buttonRemove;
        });
    }

    private ComponentRenderer getAddElement() {
        return new ComponentRenderer<>(item -> {
            WatchedProduct selectedWatchedProduct = (WatchedProduct) item;

            Button buttonAdd = new Button(new Icon(VaadinIcons.PLUS), event -> {
                Order order = new Order();
                order.setProduct(selectedWatchedProduct.getProduct());
                service.addOrder(order);
                Notification.show("Product added to shopping list.", 3000, Notification.Position.MIDDLE);
            });
            return buttonAdd;
        });
    }

    public void updateList() {
        gridWatchedProducts.setItems(service.getWatchedProducts());
    }

    public void addProduct() {
        String userInput = textFieldAddProduct.getValue();
        if (userInput.length() < 100) {
            service.addWatchedProduct(WordUtils.capitalize(userInput));
            textFieldAddProduct.clear();
            updateList();
        }
    }

}
