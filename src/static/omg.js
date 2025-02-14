/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */

(function() {
    
    // HTML template fragments
    let tagFilterSelectTemplate;
    let appliedTagTemplate;

    let itemTemplate;
    let itemTagTemplate;
    let itemIconTemplates;
    let itemStarTemplate;
    let itemPriceTemplate;
    let itemPriceDiscountTemplate;

    // HTML form elements
    let allInp;
    let audInp;
    let prnInp;
    let vidInp;
    let minPriceInp;
    let maxPriceInp;
    let minYearInp;
    let maxYearInp;
    let searchInp;
    let tagsearchInp;

    /* some persistent flags so that when loadProducts() is called, it maintains 
       some of the current state (sort and tag display) 
    */
    // current sort field, order
    let sortField = false;
    let orderField = false;

    // whether to limit tags requested or not
    let showAllTags = false;

    
    document.addEventListener('DOMContentLoaded', function() {
        tagFilterSelectTemplate = document.getElementById("tag-filter-select-template").content;
        appliedTagTemplate = document.getElementById("applied-tag-template").content;

        itemTemplate = document.getElementById("item-card-template").content;
        itemTagTemplate = document.getElementById("item-card-tag-template").content;
        itemStarTemplate = document.getElementById("item-card-star-template").content;
        itemIconTemplates = { "print" : document.getElementById("item-card-print-icon-template").content,
                              "audio" : document.getElementById("item-card-audio-icon-template").content,
                              "video" : document.getElementById("item-card-video-icon-template").content };
        itemPriceTemplate = document.getElementById("item-card-price-template").content;
        itemPriceDiscountTemplate = document.getElementById("item-card-price-discount-template").content;

        allInp = document.getElementById("all-media-check");
        audInp = document.getElementById("audio-media-check");
        prnInp = document.getElementById("print-media-check");
        vidInp = document.getElementById("video-media-check");
        minPriceInp = document.getElementById("minPrice");
        maxPriceInp = document.getElementById("maxPrice");
        minYearInp = document.getElementById("minYear");
        maxYearInp = document.getElementById("maxYear");
        searchInp = document.getElementById("searchform");
        tagsearchInp = document.getElementById("tagsearch");

        loadProducts();
        setupMediaTypeFilter();
        setupPriceYearFilter();
        setupSearchSort();
        updateCartCount();
    });


    /**
     * Updates the overall catalog page.
     */
    async function loadProducts() {
        document.getElementById("omg-item-cards").innerHTML = "";
        
        const prodlist = await fetchJSON("/api/catalog", createFilterQueryObj());
        for (const id of prodlist) {
            const data = await fetchJSON("/api/item-data", { "id" : id });
            appendItemToList(data);
        }

        updateMediaCounts();
        updatePriceYearRanges();
        updateTags();
    }


    /**
     * Collects values from the various form elements on the page to construct an
     * object with query parameters for various API calls.
     * 
     * @returns Object representing the current filter settings
     */
    function createFilterQueryObj() {
        let qobj = {}

        if (!allInp.checked) {
            let tys = [];
            for (const inp of [audInp, prnInp, vidInp]) {
                if (inp.checked) { tys.push(inp.id.substring(0,5)); }
            }
            if (tys.length < 3) {      // otherwise, it means all
                qobj["types"] = tys.join(",");
            }
        }

        if (minPriceInp.value != "") { qobj["min-price"] = minPriceInp.value; }
        if (maxPriceInp.value != "") { qobj["max-price"] = maxPriceInp.value; }
        if (minYearInp.value != "") { qobj["min-year"] = minYearInp.value; }
        if (maxYearInp.value != "") { qobj["max-year"] = maxYearInp.value; }

        if (tagsearchInp.value != "") { qobj["tag-search"] = tagsearchInp.value; }

        tags = []
        for (const sp of document.getElementById("current-tags").querySelectorAll("span")) {
            tags.push(sp.textContent.trim());
        }
        if (tags.length > 0) { qobj["tags"] = tags.join(","); } 

        if (searchInp.value != "") { qobj["search"] = searchInp.value; }

        if (sortField) { qobj["sort"] = sortField; }
        if (orderField) { qobj["order"] = orderField; }

        return qobj;
    }


    /**
     * Updates the count badges on the all/audio/video/print filter buttons.
     * This takes into account all other current filters when making the count request.
     */
    function updateMediaCounts() {
        for (const ty of ["all", "audio", "video", "print"]) {
            qobj = createFilterQueryObj();
            qobj["types"] = ty=="all" ? "audio,video,print" : ty;   // override this for each button
            fetchJSON("/api/count", qobj).then(count => {
                document.getElementById(ty + "-media-count").textContent = count;
            });    
        }        
    }


    /**
     * Updates the price and year input limits based on the current filters.
     * (Really, the server only pays attention to the current "types" parameter
     *  when calculating the current price and year ranges.)
     */
    function updatePriceYearRanges() {
        fetchJSON("/api/price-range", createFilterQueryObj()).then(priceRange => {
            if (priceRange) {
                // Prices from the server come back in whole cents.
                maxPriceInp.min = minPriceInp.min = Math.floor(priceRange["min"]/100.0);
                maxPriceInp.max = minPriceInp.max = Math.ceil(priceRange["max"]/100.0);
            }

            fetchJSON("/api/year-range", createFilterQueryObj()).then(yearRange => {
                if (yearRange) {
                    maxYearInp.min = minYearInp.min = Math.floor(yearRange["min"]);
                    maxYearInp.max = minYearInp.max = Math.ceil(yearRange["max"]);
                }
                validateRanges();
            });
        });
    }


    /**
     * Adjusts the input values to be within the current min/max ranges. If retainAndFixInvalid is true,
     * then the value is set to the min or max value, otherwise it is set to an empty string.
     * @param {boolean} retainAndFixInvalid whether to retain the value and set it to the min/max or just clear it
     */
    function validateRanges(retainAndFixInvalid, forceReload) {
        needToReload = forceReload;
        if (minPriceInp.value != "" && Number(minPriceInp.value) < Number(minPriceInp.min)) { minPriceInp.value = retainAndFixInvalid ? minPriceInp.min : ""; needToReload = true; }
        if (minPriceInp.value != "" && Number(minPriceInp.value) > Number(minPriceInp.max)) { minPriceInp.value = retainAndFixInvalid ? minPriceInp.min : ""; needToReload = true; }

        if (maxPriceInp.value != "" && Number(maxPriceInp.value) > Number(maxPriceInp.max)) { maxPriceInp.value = retainAndFixInvalid ? maxPriceInp.max : ""; needToReload = true; }
        if (maxPriceInp.value != "" && Number(maxPriceInp.value) < Number(maxPriceInp.min)) { maxPriceInp.value = retainAndFixInvalid ? maxPriceInp.max : ""; needToReload = true; }

        if (minYearInp.value != "" && Number(minYearInp.value) < Number(minYearInp.min)) { minYearInp.value = retainAndFixInvalid ? minYearInp.min : ""; needToReload = true; }
        if (minYearInp.value != "" && Number(minYearInp.value) > Number(minYearInp.max)) { minYearInp.value = retainAndFixInvalid ? minYearInp.min : ""; needToReload = true; }

        if (maxYearInp.value != "" && Number(maxYearInp.value) > Number(maxYearInp.max)) { maxYearInp.value = retainAndFixInvalid ? maxYearInp.max : ""; needToReload = true; }
        if (maxYearInp.value != "" && Number(maxYearInp.value) < Number(maxYearInp.min)) { maxYearInp.value = retainAndFixInvalid ? maxYearInp.max : ""; needToReload = true; }
        
        if (minPriceInp.value != "" && maxPriceInp.value != "" && Number(minPriceInp.value) > Number(maxPriceInp.value)) {
            minPriceInp.value = maxPriceInp.value; 
            needToReload = true;
        }
        if (minYearInp.value != "" && maxYearInp.value != "" && Number(minYearInp.value) > Number(maxYearInp.value)) {
            minYearInp.value = maxYearInp.value;
            needToReload = true;
        }

        if (needToReload) { loadProducts(); }

        return true;
    }


    /**
     * Updates the tag filter list based on current filters.
     * Tags are rendered as buttons, which when clicked, copy the tag to the current-tags area (and
     * hide it from the filter-tags area). Tags in the current-tags area can be removed, upon which
     * the corresponding one in the filter-tags area is made visible again.
     * 
     * @param {boolean} scrollToTags whether to scroll the page to the tags section upon completion
     */
    function updateTags(scrollToTags) {
        let filterTags = document.getElementById("filter-tags");
        let currentTags = document.getElementById("current-tags");
        filterTags.innerHTML = '';

        let qobj = createFilterQueryObj();
        if (!showAllTags) { qobj["tag-limit"] = 10; }  // limit tags unless expanded (the "Show all tags" button)
        fetchJSON("/api/tags", qobj).then(tags => {
            // maybe a string or a pair of [tag, count]
            for (const maybePair of tags) {
                let pair;
                if (typeof maybePair == 'string') {
                    pair = [maybePair, false];
                } else {
                    pair = maybePair;
                }
                let tagPiece = document.importNode(tagFilterSelectTemplate, true);  // clone
                let btn = tagPiece.querySelector("button");
                btn.prepend(pair[0]);
                if (pair[1] !== false) {
                    tagPiece.querySelector("span").textContent = pair[1];
                }
                filterTags.append(tagPiece);
                if (getCurrentTagList().includes(pair[0])) {    
                    btn.classList.add("d-none");    // hide the tag if it's currently in the current-tags
                }

                btn.addEventListener("click", () => {
                    let appliedTag = document.importNode(appliedTagTemplate, true);
                    appliedTag.querySelector("span").prepend(pair[0]);
                    appliedTag.querySelector("button").addEventListener("click", (evt) => {
                        currentTags.removeChild(evt.target.parentElement);
                        btn.classList.remove("d-none");
                        loadProducts();
                    });
                    currentTags.append(appliedTag);

                    btn.classList.add("d-none");
                    loadProducts();
                });
            }

            if (scrollToTags) {
                document.getElementById("sidebar").scrollIntoView( { behavior : "instant" } );
            }
        });
    }


    /**
     * Set up correct behavior of the media type filter buttons.
     * Invoke loadProducts() whenever there is any change in them.
     */
    function setupMediaTypeFilter() {
        // event listeners
        allInp.addEventListener("change", () => {
            audInp.checked = prnInp.checked = vidInp.checked = allInp.checked = true;
            loadProducts();
        });
        f = () => { 
            allInp.checked = (audInp.checked && prnInp.checked && vidInp.checked) ||
                             (!audInp.checked && !prnInp.checked && !vidInp.checked);
            if (allInp.checked) {
                audInp.checked = prnInp.checked = vidInp.checked = true;
            }
            loadProducts();
        };
        audInp.addEventListener("change", f);
        prnInp.addEventListener("change", f);
        vidInp.addEventListener("change", f);
    }


    /**
     * Invoke loadProducts() whenever there is any change in the price or year range inputs.
     */
    function setupPriceYearFilter() {
        f = () => { validateRanges(true, true); };
        minPriceInp.addEventListener("change", f);
        maxPriceInp.addEventListener("change", f);
        minYearInp.addEventListener("change", f);
        maxYearInp.addEventListener("change", f);
    }


    /**
     * Sets up the search input, sort select elements, and tag search elements.
     */
    function setupSearchSort() {
        // search input 
        f = () => {
            allInp.checked = audInp.checked = prnInp.checked = vidInp.checked = true;
            minPriceInp.value = maxPriceInp.value = minYearInp.value = maxYearInp.value = "";

            // clear current tags and tag search
            let currentTags = document.getElementById("current-tags");
            tagsearchInp.value = "";
            currentTags.innerHTML = '';

            // refresh list
            loadProducts();
        };
        searchInp.addEventListener("change", f);
        document.getElementById("searchformbutton").addEventListener("click", f);

        // tag search - updates tags
        tagsearchInp.addEventListener("input", () => {
            updateTags();
        });

        // show all tags
        document.getElementById("show-all-tags").addEventListener("click", () => {
            showAllTags = !showAllTags;
            // hide the button
            document.getElementById("show-all-tags").classList.add("d-none");
            updateTags(true);
        });

        // sort select
        let sel = document.getElementById("omg-sort-select");
        sel.addEventListener("change", () => {
            if (sel.value == "") {
                sortField = orderField = false;
            } else {
                [sortField, orderField] = sel.value.split(",");
            }
            loadProducts();
        });
    }


    /**
     * Produce the current list of tags in the current-tags area.
     * @returns Array of strings
     */
    function getCurrentTagList() {
        return Array.from(document.getElementById("current-tags").querySelectorAll("span")).map((spn) => {
                return spn.textContent.trim();
            });
    }


    /**
     * Updates the cart item count badge on the link in the header.
     */
    function updateCartCount() {
        fetchJSON("/api/cart-count").then(c => {
            document.getElementById("cart-count").innerText = c ? c : "";
        })
    }

  
    /**
     * Appends a new item card to the list of items.
     * @param {JSON} itemdata 
     */
    function appendItemToList(itemdata) {
        if (!("type" in itemdata)) {
            console.error("Item data does not have a type field", itemdata);
            return;
        }

        const itemCard = document.importNode(itemTemplate, true);  // clone
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
                priceS.querySelector("div").appendChild(document.createTextNode(itemdata["price"]["discount"]));
            }
            itemCard.querySelector(".omg-item-price-line").appendChild(priceS);
        }

        // add to cart button
        let btn = itemCard.querySelector("button.omg-add-to-cart");

        fetchJSON("/api/cart-list").then(cartlist => {
            if (!cartlist.includes(itemId)) {
                btn.classList.remove("d-none");
                btn.addEventListener("click", () => {
                    fetchJSON("/api/cart-add", { "id" : itemId }).then((success) => 
                        { 
                            if (success) {
                                btn.remove();
                            }
                            updateCartCount();
                        });
                });        
            }
        });
        
        document.getElementById("omg-item-cards").appendChild(itemCard);
    }

        
})();
