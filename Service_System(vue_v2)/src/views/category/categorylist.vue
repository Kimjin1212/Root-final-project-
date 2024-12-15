<template>
    <Nav></Nav>
    <div v-if="lastimage != undefined">



    </div>
    <!-- -->

</template>
<script setup>
import Nav from '../nav/Nav.vue'
import axios from "axios";


</script>
<script>

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
        this.getTagsList();
    },
    methods: {
        async getTagsList() {
            try {
                const response = await axios.post("http://127.0.0.1:8000/users/login", {
                    username: this.username,
                    password: this.password,
                });
                const token = response.data.token;
                
            } catch (error) {
                this.errorMessage = "Invalid username or password";
            }
        },

    }, 
};
</script>
