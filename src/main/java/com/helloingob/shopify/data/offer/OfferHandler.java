package com.helloingob.shopify.data.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.helloingob.shopify.data.beans.Product;

public class OfferHandler {

	private static OfferHandler instance;

	private OfferHandler() {
		currentOffers = new ArrayList<Offer>();
		currentOffers.addAll(getEdekaOffers());
		currentOffers.addAll(getLidlOffers());
		currentOffers.addAll(getAldiOffers());
		currentOffers.addAll(getReweOffers());
		currentOffers.addAll(getPennyOffers());
		currentOffers.addAll(getSkyOffers());
	}

	public static OfferHandler getInstance() {
		if (instance == null) {
			instance = new OfferHandler();
		}
		return instance;
	}

	List<Offer> currentOffers;

	public Map<com.helloingob.shopify.data.offer.Shop, List<Offer>> getOfferedShops(Product product) {
		Map<com.helloingob.shopify.data.offer.Shop, List<Offer>> productOffers = new TreeMap<>();

		for (Offer currentOffer : currentOffers) {
			if (currentOffer.getTitle().toLowerCase().contains(product.getTitle().toLowerCase())) {
				List<Offer> shopOffers = new ArrayList<>();
				if (productOffers.containsKey(currentOffer.getShop())) {
					shopOffers = productOffers.get(currentOffer.getShop());
				} else {
					productOffers.put(currentOffer.getShop(), shopOffers);
				}
				shopOffers.add(currentOffer);
			}
		}
		return productOffers;
	}

	private static List<Offer> getPennyOffers() {
		List<Offer> offers = new ArrayList<Offer>();
		final String BASE_URL = "http://www.penny.de";
		final String OFFER_URL = BASE_URL + "/angebote/aktuell//liste/Ab-Montag/";
		Document document;
		try {
			document = Jsoup.connect(OFFER_URL).get();
			for (Element element : document.select(".pnyProductListItem")) {
				Offer offer = new Offer();
				offer.setTitle(
						element.select(".pnyProductListItemHeader_Headline").first().text().replaceAll("\\*", ""));
				if (!element.select(".text-description").isEmpty()) {
					offer.setDescription(element.select(".text-description").first().text());
				}
				String price = element.select(".PriceWithoutBadge__Current").text().replace(",", ".").replace("ab", "")
						.replace("€", "").replaceAll("\\s", "");
				offer.setPrice(Double.parseDouble(price));
				String img = element.select(".boxformat.boxformt--productlisting").attr("style");
				offer.setImage(BASE_URL + img.substring(img.indexOf("(") + 1, img.indexOf(")")));
				offer.setShop(Shop.PENNY);
				offer.setLink(OFFER_URL);
				offers.add(offer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

	private static List<Offer> getReweOffers() {
		final String OFFER_URL = "https://www.rewe.de/angebote/";
		List<Offer> offers = new ArrayList<Offer>();
		Document document;
		try {
			document = Jsoup.connect(OFFER_URL).get();
			for (Element element : document.select(".card.drm-item-card.h-100")) {
				Offer offer = new Offer();
				if (!element.select(".headline").isEmpty()) {
					offer.setTitle(element.select(".headline").first().text());
					offer.setDescription(element.select(".text-description").first().text());
					String price = element.select(".price").text().replace(", ", ".");
					offer.setPrice(Double.parseDouble(price));
					offer.setImage(element.select(".image").attr("abs:data-src"));
					offer.setShop(Shop.REWE);
					offer.setLink(OFFER_URL);
					offers.add(offer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

	private static List<Offer> getAldiOffers() {
		final String BASE_URL = "https://www.aldi-nord.de/";
		List<Offer> offers = new ArrayList<Offer>();
		Document document;
		try {
			// Gather "aktion" links
			List<String> offerLinks = new ArrayList<String>();
			document = Jsoup.connect(BASE_URL).get();
			for (Element element : document.select(".tabs__action")) {
				offerLinks.add(element.attr("abs:href"));
			}

			// Parse each page
			for (String offerLink : offerLinks) {
				document = Jsoup.connect(offerLink).get();
				for (Element element : document.select(".mod-article-tile")) {
					Offer offer = new Offer();
					offer.setTitle(element.select("h4").first().text());
					String brand = element.select(".mod-article-tile__brand").first().text();
					if (!brand.isEmpty()) {
						offer.setTitle(brand + " " + offer.getTitle());
					}
					offer.setDescription(element.select(".mod-article-tile__info").select("p").last().text());
					offer.setPrice(
							Double.parseDouble(element.select(".price__main").first().text().replaceAll("\\*", "")));
					String img = element.select("img").attr("abs:srcset");
					offer.setImage(img.substring(0, img.indexOf("/jcr:content")));
					offer.setShop(Shop.ALDI);
					offer.setLink(offerLink);
					offers.add(offer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

	private static List<Offer> getLidlOffers() {
		final String BASE_URL = "https://www.lidl.de/de/angebote";
		List<Offer> offers = new ArrayList<Offer>();
		Document document;
		List<String> offerPages = new ArrayList<String>();
		try {
			// Get food offer page
			document = Jsoup.connect(BASE_URL).get();

			String foodOfferUrl = document.select(".cheaper").first().select("a").attr("abs:href");

			// Get all offers
			document = Jsoup.connect(foodOfferUrl).get();
			for (Element element : document.select(".offerteaser__itemlink")) {
				offerPages.add(element.select("a").attr("abs:href"));
			}
			// Extract offers from each page
			for (String offerPage : offerPages) {
				for (Element element : document.select(".product-grid__item")) {
					Offer lidlOffer = getLidlOffer(element, offerPage);
					if (lidlOffer != null) {
						offers.add(lidlOffer);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

	private static Offer getLidlOffer(Element element, String link) {
		if (element.select(".product-availability__item").attr("id").equals("store")) {
			Offer offer = new Offer();
			offer.setTitle(element.select(".desc-height").first().select("strong").text());
			offer.setDescription(element.select(".amount").first().text());
			offer.setPrice(getLidlPrice(element));
			offer.setImage(element.select(".lazy").attr("abs:data-original"));
			offer.setLink(link);
			offer.setShop(Shop.LIDL);
			return offer;
		}
		return null;
	}

	private static Double getLidlPrice(Element element) {
		String priceLabel = element.select(".pricelabel__integer").text();
		priceLabel = priceLabel.replace("-", "0");
		String priceDecimalBehind = element.select(".pricelabel__decimal-behind").text();
		priceDecimalBehind = priceDecimalBehind.replace("-", "0");
		return Double.parseDouble(priceLabel + "." + priceDecimalBehind);
	}

	private static List<Offer> getEdekaOffers() {
		final String OFFER_URL = "https://www.edeka.de/eh/angebote.jsp";
		List<Offer> offers = new ArrayList<Offer>();
		Document document;
		try {
			document = Jsoup.connect(OFFER_URL).get();
			for (Element element : document.select(".o-core-teaser-wall__teaser")) {

				if (!element.select(".a-core-copy").isEmpty()) {
					Offer offer = new Offer();

					offer.setTitle(element.select("h4").first().text());
					offer.setDescription(element.select(".a-core-copy").first().text());
					String price = element.select(".a-core-offer-price-badge").first().text().replace("€", "")
							.replaceAll("\\s", "");
					offer.setPrice(Double.parseDouble(price));
					String image = element.select(".a-core-image").attr("src");
					offer.setImage(image);
					offer.setShop(Shop.SKY);
					offer.setLink(OFFER_URL);
					offers.add(offer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

	private static List<Offer> getSkyOffers() {
		final String OFFER_URL = "https://www.sky-supermarkt.de/";
		List<Offer> offers = new ArrayList<Offer>();
		Document document;
		try {
			document = Jsoup.connect(OFFER_URL).get();
			for (Element element : document.select(".flex-active-slide")) {
				Offer offer = new Offer();
				offer.setTitle(element.select("h2").first().text());
				offer.setDescription(element.select(".description").first().text());
				String price = element.select(".price").first().text().replace(",", ".").replace("€", "")
						.replaceAll("\\s", "");
				offer.setPrice(Double.parseDouble(price));
				String image = element.select(".layout_full").attr("style");
				offer.setImage(OFFER_URL + image.substring(22, image.length()));
				offer.setShop(Shop.SKY);
				offer.setLink(OFFER_URL);
				offers.add(offer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offers;
	}

}
