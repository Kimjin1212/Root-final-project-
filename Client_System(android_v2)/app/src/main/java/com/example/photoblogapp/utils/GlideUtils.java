package com.example.photoblogapp.utils;

import com.bumptech.glide.request.RequestOptions;
import com.example.photoblogapp.R;

public class GlideUtils {
    public static RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.image)
            .error(R.drawable.image);

}
