<template>
  <div class="login-container">
    <img src="../../assets/logo.jpg" style="height: 150px; width: 300px;" />
    <h2>Login</h2>
    <el-form @submit.prevent="handleLogin">


      <el-form-item label="Username">
        <el-input v-model="username" />
      </el-form-item>

      <el-form-item label="Password">
        <el-input v-model="password" />
      </el-form-item>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <el-form-item>
        <a href="/register">Go Register</a>
        <el-button type="submit" @click="handleLogin">Login</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
      errorMessage: "",
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.post("http://127.0.0.1:8000/users/login", {
          username: this.username,
          password: this.password,
        });
        const token = response.data.token;
        localStorage.setItem("jwtToken", token); // 保存 JWT 到本地存储
        localStorage.setItem("isLoggedIn", true)
        this.$router.push("/home");
      } catch (error) {
        this.errorMessage = "Invalid username or password";
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
}

.error {
  color: red;
}
</style>
