package com.nuvola.gxpenses.client.resource.style.list;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.cellview.client.CellList;

public interface TransactionListStyle extends CellList.Resources {
    @Source({CellList.Style.DEFAULT_CSS, "com/nuvola/gxpenses/client/resource/css/transactionListStyle.css"})
    ListStyle cellListStyle();

    @Source("com/nuvola/gxpenses/client/resource/images/remove_small.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource removeSmall();

    interface ListStyle extends CellList.Style {
    }
}
