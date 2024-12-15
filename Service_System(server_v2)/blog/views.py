from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.decorators import action
from rest_framework.decorators import api_view, permission_classes

from .models import Post, Photo,Tags
from .serializers import PostSerializer, PhotoSerializer
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import AllowAny
from django.http import HttpResponse
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view
from django.db.models import Count
from itertools import chain
from django.core.paginator import Paginator
from datetime import date
from datetime import datetime
from django.utils import timezone
from datetime import timedelta

class PostViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    
    queryset = Post.objects.all()
    serializer_class = PostSerializer
    parser_classes = (MultiPartParser, FormParser) 

    # def create(self, request, *args, **kwargs):
    #     # 解析数据
    #     serializer = self.get_serializer(data=request.data)
    #     serializer.is_valid(raise_exception=True)
    #     post = serializer.save()

    #     # 处理图片上传
    #     if 'images' in request.FILES:
    #         images = request.FILES.getlist('images')
    #         for image in images:
    #             Photo.objects.create(post=post, image=image)

    #     # 返回响应
    #     return Response(serializer.data, status=status.HTTP_201_CREATED)

    # @action(detail=False, methods=['get'])
    # def recent_posts(self, request):
    #     """获取最新帖子""" 
    #     recent = Post.objects.order_by('-created_date')[:10]
    #     serializer = self.get_serializer(recent, many=True)
    #     return Response(serializer.data)

class PhotoViewSet(viewsets.ModelViewSet):  
    queryset = Photo.objects.all()
    serializer_class = PhotoSerializer 
    
    @api_view(['GET'])
    @permission_classes([IsAuthenticated])
    def getLastOne(request):  
    
        recent = Photo.objects.order_by('-created_date').first()
        serializer = PhotoSerializer(recent)  
        return Response(serializer.data)
    
    @api_view(['GET'])
    @permission_classes([IsAuthenticated])
    def queryPhotos(request):   
        # Photo.objects.all().delete()
        print(request.query_params)
        query_tag =  request.query_params.get("tag","")
        time_range =  request.query_params.get("time_range[]","")
        page =  request.query_params.get("pager",1)
        print(query_tag)
        if(len(time_range)==0 and len(query_tag) ==0 ):
            recent = Photo.objects.order_by('-created_date').all() 
        elif(len(query_tag) > 0) :
            recent = Photo.objects.filter(tags__icontains = query_tag).order_by('-created_date')
        elif(len(time_range) >0) :
            print(time_range)
            start_time_str = request.query_params.get("starttime","")
            end_time_str =  request.query_params.get("endtime","")
            print(start_time_str)
            print(end_time_str)
            start_time = datetime.strptime(start_time_str[:-1], "%Y-%m-%d %H:%M:%S") # 去掉 'Z'
            end_time = datetime.strptime(end_time_str[:-1], "%Y-%m-%d %H:%M:%S") # 去掉 'Z'
            
            recent = Photo.objects.filter(created_date__gte=start_time, created_date__lte=end_time).order_by('-created_date')
        paginator = Paginator(recent, 20) 
        page_obj = paginator.get_page(page)
        page_data = list(page_obj.object_list.values())
        return Response({
            "page": page_obj.number,
            "total_pages": paginator.num_pages,
            "total_items": paginator.count,
            "items": page_data
        })
    
    @api_view(['GET'])
    @permission_classes([IsAuthenticated])
    def getAllTags(request):  
        recent = Photo.objects.values('tags').annotate(tags_count=Count('tags'))  
        processed_data = []
        for obj in recent:
            # 使用 split 方法切割字符串
            split_values = obj["tags"].split("_")
            for item in split_values:
                processed_data.append(item)
        cleaned_arr = list(set(filter(lambda x: x.strip(), processed_data)))

        return Response(cleaned_arr)
    

    @api_view(['post'])
    @permission_classes([IsAuthenticated])
    def addTage(request):
        Tags(tags=request.data["tags"]).save()
        return HttpResponse("{ state:'1'}")


    @api_view(['post']) 
    def addPhotos(request): 
        print(request.data)
        Photo(
            created_date =  request.data["create_time"],
            tags =  request.data["tags"],
            image =  request.FILES.get('image'),
            post_id =  request.data["post"],
        ).save()
        return HttpResponse("{ state:'1'}")


    @api_view(['get'])
    @permission_classes([IsAuthenticated])
    def queryNotify(request): 
        now = timezone.now()

        # 获取一分钟前的时间
        one_minute_ago = now - timedelta(minutes=1)

        # 查询过去一分钟内的数据
        recent_data = Photo.objects.filter(created_date__gte=one_minute_ago,tags__icontains="falldown").first()
        serializer = PhotoSerializer(recent_data)    
        if(recent_data) :
            return  Response(serializer.data) 
        else:
            return Response({})

    @api_view(['get'])
    @permission_classes([IsAuthenticated])
    def getAll(self, request):
        """获取最新帖子"""
        recent = Post.objects.order_by('-created_date')[:10]
        serializer = self.get_serializer(recent, many=True)
        return Response(serializer.data)

    @api_view(['get'])
    @permission_classes([IsAuthenticated])
    def delete(self, photo_id): 
        print(photo_id) 
        photo = Photo.objects.get(id=photo_id)
        photo.delete()
        return HttpResponse("{ state:'1'}")


    @api_view(['get'])
    @permission_classes([IsAuthenticated])
    def setTage(self, tages,photo_id):
        print(photo_id) 
        photo = Photo.objects.get(id=photo_id)
        photo.tags = tages
        photo.save()
        return HttpResponse("{ state:'1'}")

