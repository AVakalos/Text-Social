PGDMP     0                	    {        
   TextSocial    15.4    15.3                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    17295 
   TextSocial    DATABASE     ~   CREATE DATABASE "TextSocial" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Greek_Greece.1253';
    DROP DATABASE "TextSocial";
                postgres    false                      0    17297    users 
   TABLE DATA           B   COPY public.users (user_id, username, password, role) FROM stdin;
    public          postgres    false    215   	                 0    17306    posts 
   TABLE DATA           @   COPY public.posts (post_id, user_id, text, created) FROM stdin;
    public          postgres    false    217   
                 0    17320    comments 
   TABLE DATA           O   COPY public.comments (comment_id, post_id, user_id, text, created) FROM stdin;
    public          postgres    false    219   �                 0    17338 	   followers 
   TABLE DATA           9   COPY public.followers (user_id, follower_id) FROM stdin;
    public          postgres    false    220   �                  0    0    comments_comment_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.comments_comment_id_seq', 10, true);
          public          postgres    false    218                       0    0    posts_post_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.posts_post_id_seq', 11, true);
          public          postgres    false    216                       0    0    users_user_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.users_user_id_seq', 4, true);
          public          postgres    false    214                 x�mлv�0 �9<�s�X�n*%"��%h�(�[���]z:����Q�o��������K4!|2�M:�44H� Z��Y������8R�1e���[��R�y�O�M8�3��|��P7P��7f��aW���?y�O�,a�!�l�7p��'�-�Lf�������ZZ���L��ο�՚n�ف8w�v���f�C�T��G��x����L@��v����H
?A�n�؁�^[�Tװ�ӤXNO�M\ͤ��q���o�(�/�LsX         �  x�m�=o�0�g�W��N!�Y[���Yt��s̄�	$���=���N<�>���R����;B9#���H���yR��2��A��Z��Nh�^(��G�b�GB�e|�=rzP��FsO;Qz��`��&�L��ئ�m���L��
ot�!ө�.!L��������;��o3��}��-�5�㶌�em��W�E'?��-��p,wt��)y�S~�; �h���C��}�g^�K*4SJ�n1�eoz�1����&�0�O�0s��e<o����N�E��/���p W���w���/w{��}t�1���+��gn�/	g���c�_��+�r~��`{��^(u�e)�.N��0q��*�_/5�G���mk�o)���L�F         &  x�mнj�0�Yz��)S�~m�K)�B��!�(��cjK�VR��UL�.�绺:���+Ba罃&���]yz �K�|#��Y!�VS9���G�a�1�Ct�lf�"2��p�*������fB�B&rC5�I�n�[�����O�����
�؅3����Q+�!6�xL�kǈ���}��2�%��pF\-�ə�f���Ў?͌qd!��'�'��;����
aW��8M�'�o��j>*�JA�%&�vp�������{]LkI���X��E�n�5�ʲ���(���k|�            x�3�4�2cc 6�2�4bc�=... 4�z     