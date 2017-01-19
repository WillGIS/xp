module api.ui.selector.list {

    export class ListBox<I> extends api.dom.UlEl {

        private items: I[] = [];

        private itemViews: {[key: string]: api.dom.Element} = {};

        private itemsAddedListeners: {(items: I[]): void}[] = [];
        private itemsRemovedListeners: {(items: I[]): void}[] = [];

        constructor(className?: string) {
            super(className);
        }

        setItems(items: I[]) {
            this.clearItems();

            this.items = items;
            if (items.length > 0) {
                this.layoutList(items);
                this.notifyItemsAdded(items);
            }
        }

        getItems(): I[] {
            return this.items;
        }

        getItem(id: string): I {
            for (let i = 0; i < this.items.length; i++) {
                let item = this.items[i];
                if (this.getItemId(item) === id) {
                    return item;
                }
            }
            return undefined;
        }

        clearItems(silent?: boolean) {
            if (this.items.length > 0) {
                let removedItems = this.items.slice();
                // correct way to empty array
                this.items.length = 0;
                this.itemViews = {};
                if (!silent) {
                    this.notifyItemsRemoved(removedItems);
                }
                this.layoutList(this.items);
            }
        }

        addItem(...items: I[]) {
            this.doAddItem(false, items);
        }

        addItems(items: I[]) {
            this.doAddItem(false, items);
        }

        addItemReadOnly(...items: I[]) {
            this.doAddItem(true, items);
        }

        private doAddItem(readOnly: boolean, items: I[]) {
            this.items = this.items.concat(items);
            items.forEach((item) => {
                this.addItemView(item, readOnly);
            });
            if (items.length > 0) {
                this.notifyItemsAdded(items);
            }
        }

        removeItem(item: I) {
            this.removeItems([item]);
        }

        removeItems(items: I[]) {
            let itemsRemoved: I[] = [];
            this.items = this.items.filter((item) => {
                for (let i = 0; i < items.length; i++) {
                    if (this.getItemId(item) === this.getItemId(items[i])) {
                        this.removeItemView(item);
                        itemsRemoved.push(item);
                        return false;
                    }
                }
                return true;
            });
            if (itemsRemoved.length > 0) {
                this.notifyItemsRemoved(itemsRemoved);
            }
        }

        getItemCount(): number {
            return this.items.length;
        }

        protected createItemView(item: I, readOnly: boolean): api.dom.Element {
            throw new Error('You must override createListItem to create views for list items');
        }

        protected getItemId(item: I): string {
            throw new Error('You must override getItemId to find item views by items');
        }

        getItemView(item: I) {
            return this.itemViews[this.getItemId(item)];
        }

        getItemViews() {
            return this.getItems().map((item) => this.getItemView(item));
        }

        refreshList() {
            this.layoutList(this.items);
        }

        private layoutList(items: I[]) {
            this.removeChildren();
            for (let i = 0; i < items.length; i++) {
                this.addItemView(items[i]);
            }
        }

        private removeItemView(item: I) {
            let itemView = this.itemViews[this.getItemId(item)];
            if (itemView) {
                this.removeChild(itemView);
                delete this.itemViews[this.getItemId(item)];
            }
        }

        private addItemView(item: I, readOnly: boolean = false) {
            let itemView = this.createItemView(item, readOnly);
            this.itemViews[this.getItemId(item)] = itemView;
            this.appendChild(itemView);
        }

        public onItemsAdded(listener: (items: I[]) => void) {
            this.itemsAddedListeners.push(listener);
        }

        public unItemsAdded(listener: (items: I[]) => void) {
            this.itemsAddedListeners = this.itemsAddedListeners.filter((current) => {
                return current !== listener;
            });
        }

        private notifyItemsAdded(items: I[]) {
            this.itemsAddedListeners.forEach((listener) => {
                listener(items);
            });
        }

        public onItemsRemoved(listener: (items: I[]) => void) {
            this.itemsRemovedListeners.push(listener);
        }

        public unItemsRemoved(listener: (items: I[]) => void) {
            this.itemsRemovedListeners = this.itemsRemovedListeners.filter((current) => {
                return current !== listener;
            });
        }

        private notifyItemsRemoved(items: I[]) {
            this.itemsRemovedListeners.forEach((listener) => {
                listener(items);
            });
        }

    }

}
