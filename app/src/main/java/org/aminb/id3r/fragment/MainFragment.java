/*
 * MainFragment.java
 * Copyright (C) 2014 Amin Bandali <me@aminb.org>
 *
 * id3r is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * id3r is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.aminb.id3r.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mpatric.mp3agic.ID3v2;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.aminb.id3r.R;
import org.aminb.id3r.activity.MainActivity;
import org.aminb.id3r.util.File;


public class MainFragment extends Fragment {

    private FloatLabeledEditText title, artist, album;
    private boolean normalMode = false;
    private File file;
    private ID3v2 tags;
    
    public MainFragment() {}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (FloatLabeledEditText) view.findViewById(R.id.id_title);
        artist = (FloatLabeledEditText) view.findViewById(R.id.id_artist);
        album = (FloatLabeledEditText) view.findViewById(R.id.id_album);

        init();
    }

    private void init() {
        if (normalMode) {

            setTextChangedListeners();

            ((MainActivity)getActivity()).setFABListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tags.setTitle(title.getTextString());
                    tags.setArtist(artist.getTextString());
                    tags.setAlbum(album.getTextString());
                    file.setTags(tags);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) getActivity()).setToolbarProgress(true);
                                }
                            });
                            if (file.save())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), getString(R.string.tag_success), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            else
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), getString(R.string.tag_fail), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity)getActivity()).setToolbarProgress(false);
                                }
                            });
                        }
                    }).start();

                }
            });

            file = new File(getActivity().getIntent().getData().getPath());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) getActivity()).setToolbarProgress(true);
                                }
                            });
                    tags = file.getTags();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tags != null) {
                                if (tags.getTitle() != null)
                                    title.setText(tags.getTitle());
                                if (tags.getArtist() != null)
                                    artist.setText(tags.getArtist());
                                if (tags.getAlbum() != null)
                                    album.setText(tags.getAlbum());

                                // hack to make hints show up
                                title.requestFieldFocus();
                                artist.requestFieldFocus();
                                album.requestFieldFocus();
                                title.requestFieldFocus();
                            }
                        }
                    });
                    getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity) getActivity()).setToolbarProgress(false);
                                }
                            });
                }
            }).start();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (getActivity().getIntent().getData() == null) { // if opened from launcher
            View view = inflater.inflate(R.layout.fragment_welcome, container, false);
            ((TextView)view.findViewById(R.id.welcome)).setText(Html.fromHtml(getString(R.string.welcome_intro)));
            return view;
        }
        else { // if opened from 'share' menu, to open a file
            normalMode = true;
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    private TextWatcher onTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            ((MainActivity)getActivity()).showFAB();
        }
    };

    private void setTextChangedListeners() {
        title.getEditText().addTextChangedListener(onTextChanged);
        artist.getEditText().addTextChangedListener(onTextChanged);
        album.getEditText().addTextChangedListener(onTextChanged);
    }
}
