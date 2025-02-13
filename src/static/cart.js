/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */

(function() {
    
    let itemCartCardTemplate;
    let itemTagTemplate;
    let itemIconTemplates;
    let itemStarTemplate;
    let itemPriceTemplate;
    let itemPriceDiscountTemplate;

    let removeCouponTemplate;


    document.addEventListener('DOMContentLoaded', function() {
        itemCartCardTemplate = document.getElementById("item-cart-card-template").content;
        itemTagTemplate = document.getElementById("item-card-tag-template").content;
        itemStarTemplate = document.getElementById("item-card-star-template").content;
        itemIconTemplates = { "print" : document.getElementById("item-card-print-icon-template").content,
                              "audio" : document.getElementById("item-card-audio-icon-template").content,
                              "video" : document.getElementById("item-card-video-icon-template").content };
        itemPriceTemplate = document.getElementById("item-card-price-template").content;
        itemPriceDiscountTemplate = document.getElementById("item-card-price-discount-template").content;
    
        removeCouponTemplate = document.getElementById("remove-coupon-button-template").content;

        setupCoupon();
        loadCart();
    });



    function setupCoupon() {
        let couponInp = document.getElementById("coupon-code");
        couponInp.addEventListener("change", () => {
            let inputCode = couponInp.value;
            if (inputCode == "") {
                loadCart();
            } else {
                fetchJSON("/api/cart-apply-coupon", { "code" : inputCode }).then((success) => {
                    if (success) {
                        couponInp.value = "";  // clear it
                        loadCart();
                    } else {
                        couponInp.value = "";  // clear it
                        document.getElementById("coupon-message").textContent = `${inputCode}: Invalid coupon code or coupon already applied.`;
                        const removeBtn = document.importNode(removeCouponTemplate, true);
                        document.getElementById("coupon-message").appendChild(removeBtn);
                        document.querySelector(".omg-remove-coupon").addEventListener("click", loadCart);
                    }
                });
            }
        });
    }

    
    function loadCart() {
        document.getElementById("omg-cart-list").innerHTML = "";
        
        fetchJSON("/api/cart-list").then((prodlist) => {
            for (const id of prodlist) {
                fetchJSON("/api/item-data", { "id" : id }).then(appendItemToList);
            }
        });

        fetchJSON("/api/cart-subtotal").then((subtotal) => {
            fetchJSON("/api/cart-total").then((total) => {
                if (subtotal && total) {
                    if (subtotal == total) {
                        document.getElementById("omg-order-total").textContent = total;
                    } else {
                        document.getElementById("omg-order-total").innerHTML = total + " <s class=\"text-muted fs-6\">" + subtotal + "</s>";
                    }
                } else {
                    document.getElementById("omg-order-total").textContent = "---";
                }
            })
        });

        fetchJSON("/api/cart-get-coupon").then((result) => {
            const msgDiv = document.getElementById("coupon-message");
            if (result) {
                const removeBtn = document.importNode(removeCouponTemplate, true);
                msgDiv.textContent = `Coupon code ${result} applied. `; 
                msgDiv.appendChild(removeBtn);

                document.getElementById("apply-coupon-div").classList.add("d-none");

                document.querySelector(".omg-remove-coupon").addEventListener("click", () => {
                    fetchJSON("/api/cart-remove-coupon", { "code" : result}).then(loadCart);
                });
            } else {
                document.getElementById("apply-coupon-div").classList.remove("d-none");
                msgDiv.innerHTML = "";
            }
        });
    }
    
    
    function appendItemToList(itemdata) {
        const itemCard = document.importNode(itemCartCardTemplate, true);  // clone
        const itemType = itemdata["type"];
        const itemId = itemdata["id"];
        
        itemCard.querySelector(".omg-item-type-icon").appendChild(document.importNode(itemIconTemplates[itemType], true));
        itemCard.querySelector(".omg-item-title").textContent = itemdata["title"]
        itemCard.querySelector(".omg-item-subtitle").textContent = getSubtitleLine(itemdata);
        itemCard.querySelector(".omg-item-info").textContent = itemdata["info-line"];

        // image
        let img = itemCard.querySelector(".omg-preview-image > img");
        if ("image" in itemdata) {
            img.src = itemdata["image"];
            img.alt = "Image of " + itemdata["title"];
        } else {
            img.classList.add("d-none");
        }

        // description
        let descrip = itemCard.querySelector(".omg-item-description");
        let shortSpan = descrip.querySelector(".omg-short");
        let fullSpan = descrip.querySelector(".omg-full");
        if ("description-short" in itemdata && "description-full" in itemdata) {
            shortSpan.prepend(itemdata["description-short"]);
            fullSpan.prepend(itemdata["description-full"]);
            
            function toggleSpans(event) {
                event.preventDefault();
                shortSpan.classList.toggle("d-none");
                fullSpan.classList.toggle("d-none");
            }

            shortSpan.querySelector("a").addEventListener("click", toggleSpans);
            fullSpan.querySelector("a").addEventListener("click", toggleSpans);
        } else {
            shortSpan.querySelector("a").classList.add("d-none");
            fullSpan.querySelector("a").classList.add("d-none");

        }

        // tags
        if ("tags" in itemdata) {
            const itemTagsP = itemCard.querySelector(".omg-item-tags");
            let taglist = itemdata["tags"];
            if (typeof taglist == 'string') { taglist = [taglist]; }
            for (const t of taglist) {
                let tagB = document.importNode(itemTagTemplate, true);
                tagB.querySelector("span").textContent = t;
                itemTagsP.appendChild(tagB);
            }
        }

        // rating
        const rateS = itemCard.querySelector(".omg-item-rating");
        if ("rating-average" in itemdata) {
            rateS.appendChild(document.createTextNode(`${itemdata["rating-average"]} `));
        }
        if ("star-icons" in itemdata) {
            for (const starico of itemdata["star-icons"]) {
                let star = document.importNode(itemStarTemplate, true);
                starico.split(" ").forEach(className => {
                    star.querySelector("i").classList.add(className);
                });
                rateS.appendChild(star);
            }
        }
        if ("rating-count" in itemdata) {
            rateS.appendChild(document.createTextNode( ` (${itemdata["rating-count"]})`));
        }

        // price
        if ("price" in itemdata) {
            let priceS;
            if (typeof itemdata["price"] == 'string' || typeof itemdata["price"] == 'number') {
                priceS = document.importNode(itemPriceTemplate, true);
                priceS.querySelector("strong").textContent = itemdata["price"];
            } else {
                priceS = document.importNode(itemPriceDiscountTemplate, true);
                priceS.querySelector("strong").textContent = itemdata["price"]["sale"];
                priceS.querySelector("s").textContent = itemdata["price"]["list"];
                priceS.querySelector("span").appendChild(document.createTextNode(itemdata["price"]["discount"]));
            }
            itemCard.querySelector(".omg-item-price-line").appendChild(priceS);
        }

        // remove from cart button
        itemCard.querySelector("button.omg-remove-btn").addEventListener("click", () => {
            fetchJSON("/api/cart-remove", { "id" : itemId }).then(loadCart);
        });
        
        document.getElementById("omg-cart-list").appendChild(itemCard);
    }


})();