
// Axios :

// -------------------------------------------------------  //
// 1. 주문처리하는 함수 선언
// -------------------------------------------------------  //
async function orderItemFn(orderItemData){
    // 요청과정
    const response = await axios.post('/order', orderItemData)
    console.log('서버로 부터 응답:',response)
    console.log('서버 응답 메시지:', response.data)

    return response;
}
// -------------------------------------------------------  //
// 2. 주문 취소하는 함수 선언
// -------------------------------------------------------  //
async function cancelOrderId(paramData){
    //console.log(paramData.orderId)

    const response = await axios.post(
                    '/order/'+paramData.orderId+'/cancel',
                    paramData)

    console.log("서버로 부터 응답 받은 객체: "+response)
    console.log("요청 응답 결과:"+response.data)

    return response;
}
// -------------------------------------------------------  //
// 3. 장바구니 담기처리하는 함수 선언
// -------------------------------------------------------  //
async function cartItem(cartItemData){
    const response = await axios.post('/cart', cartItemData)

    console.log("서버로 부터 응답 받은 객체: "+response)
    console.log("요청 응답 결과:"+response.data)

    return response;
}

  // 장바구니 상품 수량 변경 서버에 요청하기
  async function updateCartItemCountJS(cartItemId, count){
    const response = await axios.patch('/cartItem/'+ cartItemId  + '?count='+count)
    //console.log('서버로부터 응답결과:', response.data)

    return response

  }
  // 장바구니 상품 취소(삭제) 서버에 요청하기
  async function deleteCartItemJSAxios(cartItemId){
    const response = await axios.delete('/cartItem/'+cartItemId)
    return response
  }

  // 장바구니 담아 놓은 상품 주문하기
  async function ordersCartItemJSAxios(cartItems){
    const response = await axios.post('/cart/orders',cartItems)

    /*
    const response = await axios.post('/cart/orders',cartItems,
                                {
                                    headers: {'Content-Type':'application/json'}
                                })
    */
    return response
  }





/*
    csrf처리하기

// 주문 버튼 클릭시
// 형식: await axios.post('url', 전송할 데이터 객체, { headers: { 데이터형식}} )

  ex)
  await axios.post(
    '/order',               // url
    {키:"값",...} or 객체,  // 데이터
    { headers: {
        'Content-Type': "multipart/form-data" or
                        "application/json;charset=utf-8" ,  // 데이터형식
        [_csrf_header]: _csrf                               // 토큰 전송
    }})



       //           => { "cartOrderDTOList":
       //                       [{"cartItemId": cartItemId1},{"cartItemId": cartItemId2},...]
       //                 } 형식으로 List구조 문자열 서버에 전달됨
*/