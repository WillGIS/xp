module api.content {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ContentSummaryLoader extends api.util.loader.BaseLoader<api.content.json.ContentSummaryJson,api.content.ContentSummary> {

        private preservedSearchString: string;

        private contentQuery: api.content.query.ContentQuery;

        constructor(delay: number = 500) {
            this.contentQuery = new api.content.query.ContentQuery();
            var contentRequest = new api.content.ContentQueryRequest<api.content.json.ContentSummaryJson,api.content.ContentSummary>(this.contentQuery).
                setExpand(api.rest.Expand.SUMMARY);
            super(contentRequest);
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentQuery.setContentTypeNames(this.createContentTypeNames(contentTypes));
        }

        setSize(size: number) {
            this.contentQuery.setSize(size);
        }

        search(searchString: string) {

            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            var fulltextExpression: api.query.expr.Expression = api.query.FulltextSearchExpressionFactory.create(searchString);
            var queryExpr: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(fulltextExpression);
            this.contentQuery.setQueryExpr(queryExpr)

            this.load();
        }


        load() {

            this.loading(true);
            this.notifyLoadingData();

            this.sendRequest().done((contents: api.content.ContentSummary[]) => {

                this.loading(false);
                this.notifyLoadedData(contents);
                if (this.preservedSearchString) {
                    this.search(this.preservedSearchString);
                    this.preservedSearchString = null;
                }

            });
        }

        sendRequest(): Q.Promise<api.content.ContentSummary[]> {
            return this.getRequest().sendAndParse().
                then((queryResult: api.content.ContentQueryResult<api.content.ContentSummary,api.content.json.ContentSummaryJson>) => {
                    return queryResult.getContents();
                });
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {

            return (names || []).map((name: string) => new api.schema.content.ContentTypeName(name));
        }

    }

}
