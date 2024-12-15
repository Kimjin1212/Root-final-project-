from django.urls import path
from . import views
from .views import RegisterView, LoginView,HomeView

urlpatterns = [
    path('login', LoginView.as_view(), name='login'),
    path('register',  RegisterView.as_view(), name='register'),
    path('logout', views.logout_view, name='logout'),
    path('home', HomeView.as_view(), name='home'),
]