<template>
    <Nav></Nav>
    <div v-if="lastimage != undefined">


        <h1>Camera Preview :</h1>
        <h1>
            Last State : {{ lastimage.tags.replace("_","") }}
        </h1>
        <img :src="lastimage.image" style="width: 50%;height: 70%;" />


    </div>
    <!-- -->

</template>
<script setup>
import Nav from '../nav/Nav.vue'

</script>
<script>
import axios from "axios";


export default {
    data() {
        return {
            lastimage: undefined,
            imgsrc: undefined,
            username: "123123",
            intervalId: 1,
            password: "",
            errorMessage: "",
        };
    },
    mounted() {
        this.startTimer();
    },
    methods: {

        async getLastImage() {
            try {
                const response = await axios.get("http://127.0.0.1:8000/api/getlastone", {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`

                    },
                });
                response.data.image = "http://127.0.0.1:8000/" + response.data.image
                this.lastimage = response.data

            } catch (error) {
                this.errorMessage = "Invalid username or password";
            }
        },
        startTimer() {
            // 防止重复启动
            let id = setInterval(() => {
                this.getLastImage()
            }, 1000); // 每秒更新一次
            this.interval_id = id;
            console.log(this.interval_id)
            localStorage.setItem("interval_id", this.interval_id)
        },
    },
    beforeRouteLeave(to, from, next) {
        // 在离开当前路由时清理定时器
        clearInterval();
        next(); // 确保路由切换
    },
    beforeDestroy() {
        // 确保组件销毁时清除定时器
        clearInterval();
    },
};
</script>
