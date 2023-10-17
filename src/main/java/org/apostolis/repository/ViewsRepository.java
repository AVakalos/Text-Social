package org.apostolis.repository;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewsRepository {
    HashMap<String, ArrayList<String>> get_followers_posts(int id);
    HashMap<String,ArrayList<String>> get_own_posts_with_last_n_comments(int id, int max_latest_comments);
    ArrayList<String> get_all_comments_on_posts(int id);
    HashMap<String,String> get_latest_comments_on_own_or_followers_posts(int id);
    ArrayList<String> get_followers_of(int id);
    ArrayList<String> get_users_to_follow(int id);
}
