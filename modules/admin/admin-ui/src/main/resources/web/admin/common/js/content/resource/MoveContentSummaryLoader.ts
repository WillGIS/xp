module api.content.resource {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import QueryField = api.query.QueryField;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import ContentType = api.schema.content.ContentType;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class MoveContentSummaryLoader extends ContentSummaryPreLoader {

        private contentSummaryRequest: ContentSummaryRequest;

        private filterContentPath: ContentPath;

        private filterSourceContentType: ContentType;

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();
            super(this.contentSummaryRequest);
        }

        setSize(size: number) {
            this.contentSummaryRequest.setSize(size);
        }

        setFilterContentPath(filterContentPath: ContentPath) {
            this.filterContentPath = filterContentPath;
            this.contentSummaryRequest.setContentPath(this.filterContentPath);
        }

        setFilterSourceContentType(filterSourceContentType: ContentType) {
            this.filterSourceContentType = filterSourceContentType;
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {
            this.contentSummaryRequest.setSearchString(searchString);
            return this.load();
        }

        resetSearchString() {
            this.contentSummaryRequest.setSearchString("");
        }


        load(): wemQ.Promise<ContentSummary[]> {

            this.notifyLoadingData();

            return this.sendRequest().then((contents: ContentSummary[]) => {
                var deferred = wemQ.defer<ContentSummary[]>();

                var allContentTypes = contents.map((content)=> content.getType());
                var contentTypes = api.util.ArrayHelper.removeDuplicates(allContentTypes, (ct) => ct.toString());
                var contentTypeRequests = contentTypes.map((contentType)=> new GetContentTypeByNameRequest(contentType).sendAndParse());

                wemQ.all(contentTypeRequests).spread((...contentTypes: ContentType[]) => {
                    if (this.filterContentPath) {
                        contents = this.filterContent(contents, contentTypes);
                    }
                    if (contents && contents.length > 0) {
                        contents.sort(new api.content.util.ContentByPathComparator().compare);
                        this.notifyLoadedData(contents);
                    } else {
                        this.notifyLoadedData([]);
                    }

                    deferred.resolve(contents);
                }).catch((reason: any) => deferred.reject(reason)).done();

                return deferred.promise;
            });
        }

        isPartiallyLoaded(): boolean {
            return false;
        }

        private filterContent(contents: ContentSummary[], contentTypes: ContentType[]): ContentSummary[] {
            var contentTypeAllowsChild: { [s: string]: boolean; } = {};
            contentTypes.forEach((contentType)=> contentTypeAllowsChild[contentType.getName()] = contentType.isAllowChildContent());

            var createContentFilter = new api.content.util.CreateContentFilter();

            return contents.filter((content: ContentSummary) => {
                return !content.getPath().isDescendantOf(this.filterContentPath) && !this.filterContentPath.isChildOf(content.getPath()) &&
                       !this.filterContentPath.equals(content.getPath()) &&
                       contentTypeAllowsChild[content.getType().toString()] &&
                       createContentFilter.isCreateContentAllowed(content, this.filterSourceContentType);
            });
        }

    }

}
