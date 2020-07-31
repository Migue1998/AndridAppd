package com.example.sistemausuarios.data;

import android.provider.BaseColumns;

public class UsuarioContract {

    public static abstract class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME ="usuario";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String SPECIALTY = "specialty";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String AVATAR_URI = "avatarUri";
        public static final String BIO = "bio";
    }
}
