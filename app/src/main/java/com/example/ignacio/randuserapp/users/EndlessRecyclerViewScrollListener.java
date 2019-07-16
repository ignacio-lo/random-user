package com.example.ignacio.randuserapp.users;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //Cantidad minima de elementos para tener debajo de tu posicion de desplazamiento actual antes de cargar mas
    private int visibleThreshold = 5;
    //Pagina actual
    private int currentPage = 1;
    //Numero total de items despues de la ultima carga
    private int previousTotalItemCount = 0;
    //True si se esta cargando una nueva lista
    private boolean loading = true;
    //Pagina inicial
    private int startingPageIndex = 1;

    public boolean searchViewFiltering = false;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    //Metodo que se ejecuta al scrollear el RecyclerView
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        //Si el SearchView esta filtrando, salgo del metodo para evitar errores
        if (searchViewFiltering) {
            return;
        }

        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            //Posicion del ultimo item visible
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        //Si el total de la lista es cero y el previo no, la lista es invalida y se debe reiniciar
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // Si todavia esta cargando, verifico si el total de items es mayor que el previo
        // Si es mayor, asumo que termino de cargar
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        //Si no esta cargando y la posicion del ultimo item visible mas el threshold es mayor que el total de la lista, recargo
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    //Reseteo la lista si hay errores
    public void resetState(int lastPageFetched) {
        this.currentPage = lastPageFetched;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    // Metodo recargar mas
    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
