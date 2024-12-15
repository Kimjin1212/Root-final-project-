<script setup>
defineProps({

})
</script>
<template>
    <div class="greetings" v-if="isLoggedIn">
        <el-menu :default-active="activeIndex" background-color="#aaccaa22" text-color="#303133" class="el-menu-demo"
            mode="horizontal" :ellipsis="false" @select="handleSelect">
            <el-menu-item index="0">
                <img style="width: 70px" src="../../assets/logo.svg" alt="Element logo" />
                <h2>Elderly care </h2>
            </el-menu-item>
            <el-menu-item index="1">Camera preview</el-menu-item>
            <el-menu-item index="2">Photos Log</el-menu-item>
            <!-- <el-sub-menu index="3">
                <template #title>Setting</template>
                <el-menu-item index="3-1">Tag setting</el-menu-item>
            </el-sub-menu> -->
            <el-menu-item index="4">Log out</el-menu-item>
        </el-menu>
    </div>
</template>



<script>
export default {
    data() {
        return {
        }
    },
    computed: {
        isLoggedIn() {
            return localStorage.getItem('isLoggedIn') || false;
        },
        activeIndex() {
            return localStorage.getItem('activeIndex') || "1";
        }
    },

    methods: {
        handleSelect(index, path) {
            this.activeIndex = index
            localStorage.setItem('activeIndex', index);
            if (index == "1") {
                this.$router.push("/home");
            }
            if (index == "2") {
                this.$router.push("/logs");
            } if (index == "4") {
                localStorage.removeItem("isLoggedIn", false)
                localStorage.setItem('activeIndex', "1");
                this.$router.push("/login");
            }
        }
    },
};
</script>
<style scoped>
h1 {
    font-weight: 500;
    font-size: 2.6rem;
    position: relative;
    top: -10px;
}

h3 {
    font-size: 1.2rem;
}

.greetings h1,
.greetings h3 {
    text-align: center;
}

@media (min-width: 1024px) {

    .greetings h1,
    .greetings h3 {
        text-align: left;
    }
}
</style>
