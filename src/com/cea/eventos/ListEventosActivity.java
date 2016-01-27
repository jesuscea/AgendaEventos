package com.cea.eventos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Jesus on 16/05/2015.
 */
public class ListEventosActivity extends Fragment {

    View m_View;
    int m_iTabId;

    EventosAdapter m_ListAdapter;
    ListView m_ListEvents;
    String m_sTabTitle;

    public static ListEventosActivity getInstance(int position) {
        ListEventosActivity fragmentTab = new ListEventosActivity();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragmentTab.setArguments(args);

        return fragmentTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        m_View = inflater.inflate(R.layout.fragment_tab, container, false);

        m_ListEvents  = (ListView) m_View.findViewById(R.id.listEvents);
		TextView emptyText = (TextView)m_View.findViewById(android.R.id.empty);

        // Gestionamos las pestanas visibles en el evento onResume()
		m_ListEvents.setEmptyView(emptyText);

        // Registramos el menu contextual para cada lista, para mostrar la opcion de eliminar un evento
		registerForContextMenu(m_ListEvents);

        Bundle bundle = getArguments();
        if (bundle != null)
        {
            int iPos = bundle.getInt("position");
            m_iTabId = iPos;

            m_ListEvents.setAdapter(m_ListAdapter);
        }

        return m_View;
    }

    // Establece la lista a mostrar
    public void setAdapterList(EventosAdapter eventsAdapter) {
        m_ListAdapter = eventsAdapter;
        m_ListAdapter.notifyDataSetChanged();
    }


}
